package net.vash.awss3springrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.dto.StorageServiceFileDownloadDTO;
import net.vash.awss3springrestapi.exceptions.S3StorageDeleteException;
import net.vash.awss3springrestapi.exceptions.S3StorageFileNotFoundException;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.FileService;
import net.vash.awss3springrestapi.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final FileService fileService;
    @Value("${vash.bucket.name}")
    private String bucketName;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public File uploadFileForUser(MultipartFile file, String filePath, String userName) {
        String fileName = file.getOriginalFilename();
        String key = filePath + fileName;

        File fileInfo = new File();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(key);
        fileInfo = fileService.addFileForUserByUserName(fileInfo, userName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileInfo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StorageServiceFileDownloadDTO downloadFileById(Long fileId, String userName) {
        File fileToBeDownloaded = fileService.getFileById(fileId);
        if (fileToBeDownloaded == null) {
            throw new S3StorageFileNotFoundException();
        }

        String fileName = fileToBeDownloaded.getFileName();
        String fileKey = fileToBeDownloaded.getFilePath() + fileName;
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);

        File fileInfo = new File();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(fileToBeDownloaded.getFilePath());
        fileInfo.getEvent().setEventType(EventType.DOWNLOADED);
        fileService.addFileForUserByUserName(fileInfo, userName);

        return new StorageServiceFileDownloadDTO(responseBytes.asByteArray(), fileName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public File deleteFileById(Long fileId, String userName) {
        File fileToBeDeleted = fileService.getFileById(fileId);
        if (fileToBeDeleted == null) {
            throw new S3StorageFileNotFoundException();
        }

        String fileName = fileToBeDeleted.getFileName();
        String filePath = fileToBeDeleted.getFilePath();
        String fileKey = filePath + fileName;
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        File fileInfo = new File();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(filePath);
        fileInfo.getEvent().setEventType(EventType.DELETED);
        fileInfo = fileService.addFileForUserByUserName(fileInfo, userName);

        return fileInfo;
    }
}
