package net.vash.awss3springrestapi.service;

import net.vash.awss3springrestapi.dto.StorageServiceFileDownloadDTO;
import net.vash.awss3springrestapi.model.File;
import org.springframework.web.multipart.MultipartFile;

public interface S3Storage {

    File uploadFileForUser(MultipartFile file, String filePath, String userName);

    StorageServiceFileDownloadDTO downloadFileById(Long fileId, String userName);

    File deleteFileById(Long fileId, String userName);
}
