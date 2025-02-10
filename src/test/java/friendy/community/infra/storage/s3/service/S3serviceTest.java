package friendy.community.infra.storage.s3.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@DirtiesContext
class S3serviceTest {

    @Autowired
    S3service s3service;

    @Mock
    private MultipartFile mockMultipartFile;

    @Mock
    private File mockFile;

    @Test
    void uploadtest(){
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, /* 기타 PNG 파일 바이트들 */ }
        );

        String result = s3service.upload(multipartFile, "test");

        assertNotNull(result);

    }

}