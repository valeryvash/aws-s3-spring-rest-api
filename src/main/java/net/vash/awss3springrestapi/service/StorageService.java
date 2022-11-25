package net.vash.awss3springrestapi.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    boolean uploadFile(MultipartFile file, String fileName);

    byte[] getFileByFileName(String fileName);

    boolean deleteFileByFileName(String fileName);
}
