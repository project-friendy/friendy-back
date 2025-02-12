package friendy.community.infra.storage.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import friendy.community.infra.storage.s3.exception.S3exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3service {


    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3exception s3exception;

    public String upload(MultipartFile multipartFile, String dirName) {

        s3exception.validateFile(multipartFile);

        return putS3(multipartFile, generateStoredFileName(multipartFile,dirName));
    }

    private String putS3(MultipartFile multipartFile, String uuidFileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(bucket, uuidFileName, inputStream, metadata); // UUID 파일명으로 S3에 저장
        } catch (IOException e) {
            throw new FriendyException(ErrorCode.FILE_IO_ERROR, "S3 업로드 중 오류 발생");
        }
        return s3Client.getUrl(bucket, uuidFileName).toString(); // UUID 파일명으로 URL 반환
    }

    public String generateStoredFileName(MultipartFile multipartFile, String dirName) {
        // MultipartFile에서 원본 파일 이름을 가져오기
        String originalFileName = multipartFile.getOriginalFilename();

        if (originalFileName == null) {
            throw new FriendyException(ErrorCode.INVALID_FILE ,"파일 이름을 가져올 수 없습니다.");
        }
        // 원본 파일 이름에서 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // UUID를 이용해 고유한 파일 이름 생성
        String uuid = UUID.randomUUID().toString();
        // 고유한 파일 이름 반환
        return dirName + "/" + uuid + extension; // 예: 123e4567-e89b-12d3-a456-426614174000.jpg
    }

    public String moveS3Object(String imageUrl, String newDirName) {
        String oldKey = extractFilePath(imageUrl);

        // 2. 새 경로로 객체 키 생성
        String fileName = oldKey.substring(oldKey.lastIndexOf("/") + 1); // 파일명만 추출
        String newKey = newDirName + "/" + fileName; // 새로운 경로 생성

        // 3. 객체 복사 (기존 위치 → 새 위치)
        copyObject(bucket, oldKey, bucket, newKey);

        return s3Client.getUrl(bucket, newKey).toString();

    }

    private void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
            sourceBucket, sourceKey,
            destinationBucket, destinationKey);
        try {
            s3Client.copyObject(copyObjectRequest);
        } catch (FriendyException e) {
            throw new FriendyException(ErrorCode.INTERNAL_SERVER_ERROR,"S3 객체 복사에 실패했습니다");
        }
    }

    public String extractFilePath(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            return path.startsWith("/") ? path.substring(1) : path; // 앞의 '/' 제거
        } catch (MalformedURLException e) {
            throw new FriendyException(ErrorCode.INVALID_FILE, "유효한 URL 형식이어야 합니다.");
        }
    }


    public String getContentTypeFromS3(String key) {
        try {
            S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));
            return object.getObjectMetadata().getContentType();
        } catch (Exception e) {
            throw new FriendyException(ErrorCode.INVALID_FILE, "파일타입을 가져올수 없습니다.");

        }
    }
}

