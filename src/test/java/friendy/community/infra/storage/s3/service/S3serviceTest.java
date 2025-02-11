package friendy.community.infra.storage.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import friendy.community.infra.storage.s3.exception.S3exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@SpringBootTest
class S3serviceTest {

    @Autowired
    private S3service s3service;  // 실제 테스트 대상 클래스

    @MockitoBean
    private AmazonS3 s3Client;  // AmazonS3 mock 객체

    @Mock
    private S3exception s3exception;  // S3exception mock 객체

    @Mock
    private File file;

    @Mock
    MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    void upload_성공() throws MalformedURLException {
        // Given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        String tempUrl = "https://your-bucket.s3.amazonaws.com/test.jpg";
        when(s3Client.getUrl(anyString(), anyString())).thenReturn(new URL(tempUrl));

        // When
        String actualUrl = s3service.upload(multipartFile, "test-dir");

        // Then
        assertThat(actualUrl).isEqualTo(tempUrl);
    }

    @Test
    public void testConvert_FileCreationFailure() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        when(file.createNewFile()).thenReturn(false); // 파일 생성 실패 시뮬레이션

        // when, then
        assertThrows(FriendyException.class, () -> {
            s3service.convert(multipartFile);
        }, "파일 생성에 실패했습니다.");
    }


    @Test
    void moveS3Object_성공() throws MalformedURLException {
        // Given
        String imageUrl = "https://your-bucket.s3.amazonaws.com/old-dir/test.jpg";
        String newDirName = "new-dir";
        String expectedUrl = "https://your-bucket.s3.amazonaws.com/new-dir/test.jpg";

        when(s3Client.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));

        // When
        String actualUrl = s3service.moveS3Object(imageUrl, newDirName);

        // Then
        assertThat(actualUrl).isEqualTo(expectedUrl);
        verify(s3Client).copyObject(any()); // copyObject 호출 확인
    }

    @Test
    void testGenerateStoredFileName_FileNameNotFoundException() {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        assertThrows(FriendyException.class, () -> {
            s3service.generateStoredFileName(multipartFile,"upload");
        }, "파일 이름을 가져올 수 없습니다.");

    }

    @Test
    public void testConvert_WriteFileFailure() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        when(file.createNewFile()).thenReturn(true);
        when(new FileOutputStream(any(File.class))).write(any(byte[].class))).thenThrow(new IOException("디스크 공간 부족"));

        // when, then
        assertThrows(FriendyException.class, () -> {
            s3service.convert(multipartFile);
        }, "I/O 오류 발생");
    }


}