package net.vash.awss3springrestapi.service;

import net.vash.awss3springrestapi.model.File;

public interface FileService {
    File addFileForUserByUserName(File file, String userName);

    File getFileById(Long fileId);
}
