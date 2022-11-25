package net.vash.awss3springrestapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.vash.awss3springrestapi.dto.UserSignUpRequestDTO;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.security.jwt.JwtTokenProvider;
import net.vash.awss3springrestapi.service.UserService;
import net.vash.awss3springrestapi.service.exceptions.FieldsAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserControllerV1.class)
class UserControllerV1Test {
    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserControllerV1 userControllerV1;

    private User returnedUser;
    private UserSignUpRequestDTO requestDTO;

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        returnedUser = new User();

        returnedUser.setId(2L);
        returnedUser.setUserName("someName");
        returnedUser.setPassword("somePass");
        returnedUser.setFirstName("some1stName");
        returnedUser.setLastName("someLastName");
        returnedUser.setEmail("some@email.com");

        requestDTO = UserSignUpRequestDTO.fromUser(returnedUser);

        objectMapper = spy(new JsonMapper());
    }

    @Test
    void sigUpThrowsExceptionWhileNullPassed() {
        assertThrows(NullPointerException.class, () -> userControllerV1.signInUser(null));

        verifyNoInteractions(userService);
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void sigUp() {
        when(userService.signUp(any(User.class))).thenReturn(returnedUser);

        ResponseEntity<?> responseEntity = userControllerV1.signUpUser(requestDTO);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void sigUpInternalServerError() {
        when(userService.signUp(any(User.class))).thenThrow(IllegalArgumentException.class);

        try {
            userControllerV1.signUpUser(requestDTO);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
        }

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void sigUpBadRequestError() {
        when(userService.signUp(any(User.class))).thenThrow(FieldsAlreadyExistException.class);

        try {
            userControllerV1.signUpUser(requestDTO);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }
}
