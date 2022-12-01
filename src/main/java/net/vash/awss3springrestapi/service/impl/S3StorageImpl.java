package net.vash.awss3springrestapi.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.security.jwt.JwtUser;
import net.vash.awss3springrestapi.service.FileService;
import net.vash.awss3springrestapi.service.S3Storage;
import net.vash.awss3springrestapi.service.exceptions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class S3StorageImpl implements S3Storage {
    private final S3Client s3Client;
    private final FileService fileService;
    @Value("${vash.bucket.name}")
    private String bucketName;

    public S3StorageImpl(S3Client s3Client, FileService fileService) {
        this.s3Client = s3Client;
        this.fileService = fileService;
    }

    @Override
    public File uploadFileForUser(MultipartFile multipartFile, String filePath, String userName) {
        String fileName = multipartFile.getName();
        String key = filePath + fileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));
            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("IN uploadFileForUser file uploaded to storage successfully. File key is: {}", key);
                File uploadedFileInfo = new File();
                uploadedFileInfo.setFileName(fileName);
                uploadedFileInfo.setFilePath(filePath);
                uploadedFileInfo = fileService.addFileForUserByUserName(uploadedFileInfo, userName);
                return uploadedFileInfo;
            } else {
                log.warn("IN uploadFileForUser file upload to storage error occurred. File key is: {}", key);
                throw new StorageUploadException();
            }
        } catch (SdkException e) {
            log.warn("IN uploadFileForUser SDK exception error occurred. File name: {}, file path {}", fileName,filePath);
            e.printStackTrace();
            throw e;
        } catch (FileSaveException e) {
            log.warn("IN uploadFileForUser fileInfo save exception occurred. File key is: {}", key);
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            log.warn("IN uploadFileForUser IOException occurred. File key is: {}", key);
            e.printStackTrace();
            throw new S3StorageIOException();
        }
    }

    @Override
    public byte[] downloadFileById(Long fileId, String userName) {
        File fileToBeDownloaded = fileService.getFileById(fileId);

        if (fileToBeDownloaded == null) {
            log.warn("IN downloadFileById fileInfo not found by file id: {}", fileId);
            throw new S3StorageFileNotFoundException();
        }

        String fileKey = fileToBeDownloaded.getFilePath() + fileToBeDownloaded.getFileName();

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
            log.info("IN downloadFileById success fileId {} fileKey {} ", fileId, fileKey);
            File downloadFileInfo = new File();
            downloadFileInfo.setFileName(fileToBeDownloaded.getFileName());
            downloadFileInfo.setFilePath(fileToBeDownloaded.getFilePath());
            downloadFileInfo.getEvent().setEventType(EventType.DOWNLOADED);
            fileService.addFileForUserByUserName(downloadFileInfo, userName);
            return responseBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            log.warn("IN downloadFileById file not found in S3 storage fileId {} fileKey {} ", fileId, fileKey);
            e.printStackTrace();
            throw new S3StorageFileNotFoundException();
        }catch (SdkException e) {
            log.warn("IN downloadFileById SdkException occurred fileId {} fileKey {} ", fileId, fileKey);
            e.printStackTrace();
            throw new S3StorageFileDownloadException();
        } catch (FileSaveException e) {
            log.warn("IN downloadFileById downloadFileInfo save exception occurred. File key is: {}", fileKey);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public File deleteFileById(Long fileId, String userName) {
        File fileToBeDeleted = fileService.getFileById(fileId);

        if (fileToBeDeleted == null) {
            log.warn("IN deleteFileById file not found, fileId: {}", fileId);
            throw new S3StorageFileNotFoundException();
        }

        String fileKey = fileToBeDeleted.getFilePath() + fileToBeDeleted.getFileName();
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try {
            DeleteObjectResponse response = s3Client.deleteObject(request);

            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("IN deleteFileById file with fileKey {} deleted successfully", fileKey);
                File deletedFileInfo = new File();
                deletedFileInfo.setFileName(fileToBeDeleted.getFileName());
                deletedFileInfo.setFilePath(fileToBeDeleted.getFilePath());
                deletedFileInfo.getEvent().setEventType(EventType.DELETED);
                deletedFileInfo = fileService.addFileForUserByUserName(deletedFileInfo, userName);
                return deletedFileInfo;
            } else {
                log.warn("IN deleteFileById file not deleted successfully, file key: {}", fileKey);
                throw new S3StorageDeleteException();
            }
        } catch (SdkException e) {
            log.warn("SdkException occurred during delete the file with name {}", fileKey);
            throw e;
        } catch (FileSaveException e) {
            log.warn("IN downloadFileById downloadFileInfo save exception occurred. File key is: {}", fileKey);
            e.printStackTrace();
            throw e;
        }
    }
}
