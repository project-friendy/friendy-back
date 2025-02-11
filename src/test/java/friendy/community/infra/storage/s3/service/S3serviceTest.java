package friendy.community.infra.storage.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import friendy.community.infra.storage.s3.exception.S3exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void upload_실패_빈파일() {
        // Given: MockMultipartFile을 비어있는 파일로 설정
        MockMultipartFile multipartFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        // When: s3exception.validateFile() 메소드가 빈 파일일 때 FriendyException을 던지도록 설정
        doThrow(new FriendyException(ErrorCode.INVALID_FILE, "파일이 비어 있습니다.")).when(s3exception).validateFile(any(MultipartFile.class));

        // Then: validateFile에서 FriendyException이 발생하는지 검증
        assertThrows(FriendyException.class, () -> {
            s3service.upload(multipartFile, "test-dir");  // 빈 파일일 경우 예외가 던져져야 함
        });
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



}