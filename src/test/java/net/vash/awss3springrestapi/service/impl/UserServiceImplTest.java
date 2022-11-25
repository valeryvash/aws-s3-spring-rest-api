package net.vash.awss3springrestapi.service.impl;

import net.vash.awss3springrestapi.model.Role;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.repository.RoleRepo;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserServiceImpl.class})
class UserServiceImplTest {

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private RoleRepo roleRepo;
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    private static User tempUser;
    private static Role tempRole;

    @BeforeEach
    void setUp() {
        tempUser = new User();

        tempUser.setUserName("userName");
        tempUser.setPassword("somePass");
        tempUser.setFirstName("someFirstName");
        tempUser.setLastName("someLastName");
        tempUser.setEmail("some@email.com");

        tempRole = new Role();

        tempRole.setRoleName("SOME_ROLE_NAME");
    }

    @Test
    void findByUserName() {
        when(userRepo.findByUserNameIgnoreCase(isNull())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.findByUserName(null));

        verify(userRepo,times(1)).findByUserNameIgnoreCase(isNull());


        when(userRepo.findByUserNameIgnoreCase(any(String.class))).thenReturn(tempUser);

        assertDoesNotThrow(() -> userService.findByUserName("someName"));

        verify(userRepo,times(1)).findByUserNameIgnoreCase(isNotNull());
    }

    @Test
    void singUpNullArg() {
        when(roleRepo.findByRoleNameIgnoreCase(anyString())).thenReturn(tempRole);

        when(userRepo.save(isNull())).thenThrow(RuntimeException.class);

        assertThrows(IllegalArgumentException.class, () -> userService.signUp(null));

        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(userRepo);
    }

    @Test
    void singUp() {
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenReturn(tempUser);

        userService.signUp(tempUser);

        verify(userRepo, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepo);
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verifyNoMoreInteractions(bCryptPasswordEncoder);
    }

    @Test
    void singUpUserRepoException() {
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepo.save(tempUser)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> userService.signUp(tempUser));

        verify(userRepo, times(1)).save(tempUser);
        verifyNoMoreInteractions(userRepo);
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verifyNoMoreInteractions(bCryptPasswordEncoder);

    }

    @Test
    void update() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(null));
    }

    @Test
    void updateUserNotFoundInRepo() {
        when(userRepo.findByUserNameIgnoreCase(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.update(tempUser));

        verify(userRepo, times(1)).findByUserNameIgnoreCase(anyString());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void updateFailsWhenFieldsAlreadyExist() {
        when(userRepo.findByUserNameIgnoreCase(anyString())).thenReturn(tempUser);
        when(userRepo.save(tempUser)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> userService.update(tempUser));

        verify(userRepo, times(1)).findByUserNameIgnoreCase(anyString());
        verify(userRepo, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void deleteByUserNameNullArgument() {
        when(userRepo.findByUserNameIgnoreCase(isNull())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.deleteByUserName(null));

        verify(userRepo, times(1)).findByUserNameIgnoreCase(isNull());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void deleteByUserNameStorageException() {
        when(userRepo.findByUserNameIgnoreCase(anyString())).thenReturn(tempUser);
        doThrow(RuntimeException.class).when(userRepo).delete(any(User.class));

        assertThrows(RuntimeException.class, () -> userService.deleteByUserName("someString"));

        verify(userRepo, times(1)).findByUserNameIgnoreCase(anyString());
        verify(userRepo,times(1)).delete(any(User.class));
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void deleteByUserName() {
        when(userRepo.findByUserNameIgnoreCase(anyString())).thenReturn(tempUser);
        doNothing().when(userRepo).delete(any(User.class));

        assertSame(tempUser, userService.deleteByUserName("someString"));

        verify(userRepo, times(1)).findByUserNameIgnoreCase(anyString());
        verify(userRepo,times(1)).delete(any(User.class));
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void existsMethods() {
        when(userRepo.existsByUserNameIgnoreCase(anyString())).thenReturn(true);
        when(userRepo.existsByUserNameIgnoreCase(isNull())).thenReturn(false);

        when(userRepo.existsByEmailIgnoreCase(anyString())).thenReturn(true);
        when(userRepo.existsByEmailIgnoreCase(isNull())).thenReturn(false);

        userService.isUserExistByEmailIgnoreCase("non_null");
        userService.isUserExistByEmailIgnoreCase(null);

        userService.isUserExistByUserNameIgnoreCase("non_null");
        userService.isUserExistByUserNameIgnoreCase(null);

        verify(userRepo, times(1)).existsByEmailIgnoreCase(anyString());
        verify(userRepo, times(1)).existsByEmailIgnoreCase(isNull());
        verify(userRepo, times(1)).existsByUserNameIgnoreCase(anyString());
        verify(userRepo, times(1)).existsByUserNameIgnoreCase(isNull());
        verifyNoMoreInteractions(userRepo);
    }

}