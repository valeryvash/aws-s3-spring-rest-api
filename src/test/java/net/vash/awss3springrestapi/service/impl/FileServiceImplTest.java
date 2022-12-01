package net.vash.awss3springrestapi.service.impl;

import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.repository.FileRepo;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileServiceImpl.class})
class FileServiceImplTest {

    @MockBean
    private FileRepo fileRepo;

    @MockBean
    private UserRepo userRepo;

    @Autowired
    private FileService fileService;

    private static File file;

    private static User tempUser;

    @BeforeEach
    void beforeEach() {
        tempUser = new User();

        tempUser.setUserName("userName");
        tempUser.setPassword("somePass");
        tempUser.setFirstName("someFirstName");
        tempUser.setLastName("someLastName");
        tempUser.setEmail("some@email.com");

        file = new File();

        file.setFileName("some_file_name");

    }

    @Test
    void addFileForUserByUserNameThrowsExceptionForNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> fileService.addFileForUserByUserName(file, null));
        assertThrows(IllegalArgumentException.class, () -> fileService.addFileForUserByUserName(null, "some user name"));
        assertThrows(IllegalArgumentException.class, () -> fileService.addFileForUserByUserName(null,null));

        verifyNoInteractions(userRepo);
        verifyNoInteractions(fileRepo);
    }

    @Test
    void addFileForUserByUserNameThrowsExceptionForNotExistedUser() {
        when(userRepo.findByUserName(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> fileService.addFileForUserByUserName(file, "some_user_name"));

        verify(userRepo, times(1)).findByUserName(anyString());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(fileRepo);
    }

    @Test
    void addFileForUserByUserNameThrowsFileSaveException() {
        when(userRepo.findByUserName(anyString())).thenReturn(tempUser);
        when(fileRepo.save(any(File.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> fileService.addFileForUserByUserName(file,"someUserName"));

        verify(userRepo, times(1)).findByUserName(anyString());
        verifyNoMoreInteractions(userRepo);
        verify(fileRepo, times(1)).save(any(File.class));
        verifyNoMoreInteractions(fileRepo);
    }

    @Test
    void addFileForUserByUserName() {
        when(userRepo.findByUserName(anyString())).thenReturn(tempUser);
        when(fileRepo.save(any(File.class))).thenReturn(file);

        fileService.addFileForUserByUserName(file, "some_userName");

        verify(userRepo, times(1)).findByUserName(anyString());
        verifyNoMoreInteractions(userRepo);
        verify(fileRepo, times(1)).save(any(File.class));
        verifyNoMoreInteractions(fileRepo);
    }
}