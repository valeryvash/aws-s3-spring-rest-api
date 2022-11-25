package net.vash.awss3springrestapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.repository.FileRepo;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.service.FileService;
import net.vash.awss3springrestapi.service.exceptions.FileSaveException;
import net.vash.awss3springrestapi.service.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepo fileRepo;

    private final UserRepo userRepo;

    public FileServiceImpl(FileRepo fileRepo, UserRepo userRepo) {
        this.fileRepo = fileRepo;
        this.userRepo = userRepo;
    }

    @Override
    public File addFileForUserByUserName(File file, String userName) {
        if (userName == null || file == null) {
            log.warn("IN addFileForUserByUserName method null argument passed");
            throw new IllegalArgumentException();
        }

        User userFilesToBeUpdated = userRepo.findByUserNameIgnoreCase(userName);

        if (userFilesToBeUpdated == null) {
            log.warn("IN addFileForUserByUserName user not found for username: {} ", userName);
            throw new UserNotFoundException();
        }

        try {
            file.getEvent().setUser(userFilesToBeUpdated);
            File fileToBeReturned = fileRepo.save(file);
            log.info("IN addFileForUserByUserName method user files updated successfully. User name: {} ", userName);
            return fileToBeReturned;
        } catch (RuntimeException e) {
            log.warn("IN addFileForUserByUserName method runtime exception occurred");
            throw new FileSaveException();
        }
    }

}
