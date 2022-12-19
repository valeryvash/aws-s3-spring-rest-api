package net.vash.awss3springrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.dto.StorageServiceFileDownloadDTO;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.FileService;
import net.vash.awss3springrestapi.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final FileService fileService;
    @Value("${vash.bucket.name}")
    private String bucketName;

    @Override
    @Transactional
    public File uploadFileForUser(MultipartFile file, String filePath, String userName) {

        return null;
    }

    @Override
    public StorageServiceFileDownloadDTO downloadFileById(Long fileId, String userName) {
        return null;
    }

    @Override
    public File deleteFileById(Long fileId, String userName) {
        return null;
    }
}
