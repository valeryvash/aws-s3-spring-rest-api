package net.vash.awss3springrestapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;

    @Value("${vash.bucket.name}")
    private String bucketName;

    public StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public boolean uploadFile(MultipartFile multipartFile, String fileName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        File file = convertMultipartFileToFile(multipartFile);

        try {
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(file));

            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("File uploaded successfully. File name: {}, fileName{}", file.getName(),fileName);
                return true;
            } else {
                log.warn("File wasn't uploaded successfully. File name: {}, fileName{}", file.getName(),fileName);
                return false;
            }
        } catch (SdkException e) {
            log.warn("Some SdkException occurred during file upload. File name: {}, fileName{}", file.getName(),fileName);
            return false;
        }
    }

    @Override
    public byte[] getFileByFileName(String fileName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
            log.info("File download initiated successfully for fileName {} ",fileName);
            return responseBytes.asByteArray();
        } catch (SdkException e) {
            log.warn("SdkException occurred during getting the file with name {} ",fileName);
            return new byte[0];
        }
    }

    @Override
    public boolean deleteFileByFileName(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            DeleteObjectResponse response = s3Client.deleteObject(request);

            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("File with fileName {} deleted successfully",fileName);
                return true;
            } else {
                log.warn("File with fileName {} WASN'T deleted successfully", fileName);
                return false;
            }
        } catch (SdkException e) {
            log.warn("SdkException occurred during delete the file with name {}",fileName);
            return false;
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File fileResult = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(fileResult)) {
            fos.write(multipartFile.getBytes());
            return fileResult;
        } catch (IOException e) {
            log.error(" Exception in StorageService class {} method convert", e.getStackTrace());
        }
        log.warn("IN StorageService convertMultiPartToFile File path is {}",fileResult.getPath());
        return fileResult;
    }
}
