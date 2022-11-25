package net.vash.awss3springrestapi.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.controller.UserControllerV1;
import net.vash.awss3springrestapi.dto.UserSignInRequestDTO;
import net.vash.awss3springrestapi.dto.UserSignUpRequestDTO;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.security.jwt.JwtTokenProvider;
import net.vash.awss3springrestapi.service.UserService;
import net.vash.awss3springrestapi.service.exceptions.FieldsAlreadyExistException;
import net.vash.awss3springrestapi.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserControllerV1.class)
@Slf4j
class UserControllerV1Test {

    private static final String API_VERSION_MAPPING = "/api/v1";
    private static final String USER_SIGNUP_MAPPING = API_VERSION_MAPPING + "/auth/signup";
    private static final String USER_SIGNIN_MAPPING = API_VERSION_MAPPING + "/auth/signin";

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private AuthenticationManager authenticationManager;

    private User returnedUser;
    private UserSignUpRequestDTO signUpRequestDTO;
    private UserSignInRequestDTO signInRequestDTO;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void beforeEach() {
        returnedUser = new User();

        returnedUser.setId(2L);
        returnedUser.setUserName("someName");
        returnedUser.setPassword("somePass");
        returnedUser.setFirstName("some1stName");
        returnedUser.setLastName("someLastName");
        returnedUser.setEmail("some@email.com");

        signUpRequestDTO = UserSignUpRequestDTO.fromUser(returnedUser);
        signInRequestDTO = UserSignInRequestDTO.fromUser(returnedUser);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void sinUpTest() {
        when(userService.signUp(any(User.class))).thenReturn(returnedUser);

        mockMvc
                .perform(
                        post(USER_SIGNUP_MAPPING)
                                .content(objectMapper.writeValueAsString(signUpRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.CREATED.value()));

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoMoreInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void sinUpTestThrownInternalError() {
        when(userService.signUp(any(User.class))).thenThrow(IllegalArgumentException.class);

        mockMvc
                .perform(
                        post(USER_SIGNUP_MAPPING)
                                .content(objectMapper.writeValueAsString(signUpRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoMoreInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void sinUpTestThrownBadRequestError() {
        when(userService.signUp(any(User.class))).thenThrow(FieldsAlreadyExistException.class);

        mockMvc
                .perform(
                        post(USER_SIGNUP_MAPPING)
                                .content(objectMapper.writeValueAsString(signUpRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        verify(userService, times(1)).signUp(any(User.class));
        verifyNoMoreInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void signInTest() {
        Authentication authMock = mock(Authentication.class);

        when(userService.findByUserName(any(String.class))).thenReturn(returnedUser);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("someToken");

        mockMvc
                .perform(
                        post(USER_SIGNIN_MAPPING)
                                .content(objectMapper.writeValueAsString(signInRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Authorization", "Bearer_someToken"))
        ;

        verify(userService, times(1)).findByUserName(any(String.class));
        verifyNoMoreInteractions(userService);

        verify(jwtTokenProvider, times(1)).createToken(anyString(), anyList());
        verifyNoMoreInteractions(jwtTokenProvider);

        verify(authenticationManager, times(1)).authenticate(any());
        verifyNoMoreInteractions(authenticationManager);

        verifyNoInteractions(authMock);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void signInTestBadRequestWhileArgIsNull() {
        Authentication authMock = mock(Authentication.class);

        when(userService.findByUserName(any(String.class))).thenReturn(returnedUser);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("someToken");

        List<UserSignInRequestDTO> dtos =
                List.of(
                        new UserSignInRequestDTO(null, null),
                        new UserSignInRequestDTO(null, ""),
                        new UserSignInRequestDTO("", null),
                        new UserSignInRequestDTO("", "")
                );

        for (UserSignInRequestDTO requestDTO :
                dtos) {
            mockMvc
                    .perform(
                            post(USER_SIGNIN_MAPPING)
                                    .content(objectMapper.writeValueAsString(requestDTO))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }

        verifyNoInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);

        verifyNoInteractions(authenticationManager);

        verifyNoInteractions(authMock);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void signInTestUserNotFound() {
        Authentication authMock = mock(Authentication.class);

        when(userService.findByUserName(any(String.class))).thenThrow(UserNotFoundException.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("someToken");

        mockMvc
                .perform(
                        post(USER_SIGNIN_MAPPING)
                                .content(objectMapper.writeValueAsString(signInRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));


        verify(userService, times(1)).findByUserName(anyString());
        verifyNoMoreInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);

        verifyNoInteractions(authenticationManager);

        verifyNoInteractions(authMock);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void signInTestBadRequestAuth() {
        Authentication authMock = mock(Authentication.class);

        class CustomAuthenticationException extends AuthenticationException {

            public CustomAuthenticationException(String msg) {
                super(msg);
            }
        }

        when(userService.findByUserName(any(String.class))).thenReturn(returnedUser);
        when(authenticationManager.authenticate(any())).thenThrow(new CustomAuthenticationException("Something goes wrong"));
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("someToken");

        mockMvc
                .perform(
                        post(USER_SIGNIN_MAPPING)
                                .content(objectMapper.writeValueAsString(signInRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));


        verify(userService, times(1)).findByUserName(anyString());
        verifyNoMoreInteractions(userService);

        verifyNoInteractions(jwtTokenProvider);

        verify(authenticationManager,times(1)).authenticate(any());
        verifyNoMoreInteractions(authenticationManager);

        verifyNoInteractions(authMock);
    }



}