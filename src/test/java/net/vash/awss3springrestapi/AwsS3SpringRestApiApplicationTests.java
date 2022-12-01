package net.vash.awss3springrestapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootTest
@Slf4j
class AwsS3SpringRestApiApplicationTests {
    @Autowired
    private S3Client s3Client;

    @Value("${vash.bucket.name}")
    private String bucketName;


    @Test
    void contextLoads() {
    }

    @Test
    void s3Test() {
        String fileName = "someName";

        MockMultipartFile multipartFileMock = new MockMultipartFile("Some name", new byte[0]);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        File file = convertMultipartFileToFile(multipartFileMock);

        PutObjectResponse responce = s3Client.putObject(request, RequestBody.fromFile(file));



    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File fileResult = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(fileResult)) {
            fos.write(multipartFile.getBytes());
            return fileResult;
        } catch (IOException e) {
            log.error(" Exception in StorageService class {} method convert", e.getStackTrace());
        }
        log.warn("IN StorageService convertMultiPartToFile File path is {}", fileResult.getPath());
        return fileResult;
    }
}
