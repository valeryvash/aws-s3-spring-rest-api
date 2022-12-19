package net.vash.awss3springrestapi.controller;

import net.vash.awss3springrestapi.dto.UserSignInRequestDTO;
import net.vash.awss3springrestapi.dto.UserSignInResponseDTO;
import net.vash.awss3springrestapi.dto.UserSignUpRequestDTO;
import net.vash.awss3springrestapi.dto.UserSignUpResponseDTO;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.security.jwt.JwtTokenProvider;
import net.vash.awss3springrestapi.service.UserService;
import net.vash.awss3springrestapi.exceptions.FieldsAlreadyExistException;
import net.vash.awss3springrestapi.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(
        path = "/api/v1",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserControllerV1 {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    public UserControllerV1(UserService userService,
                            JwtTokenProvider jwtTokenProvider,
                            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(
            path = "/auth/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSignUpResponseDTO> signUpUser(@RequestBody UserSignUpRequestDTO requestDTO) {
        User userToBeRegistered = requestDTO.fromDTO();

        try {
            userToBeRegistered = userService.signUp(userToBeRegistered);

            UserSignUpResponseDTO dtoToBeReturned = UserSignUpResponseDTO.fromUser(userToBeRegistered);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(dtoToBeReturned);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Null user passed to persistent context"
            );
        } catch (FieldsAlreadyExistException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Looks like some parameters already exist in persistent context"
            );
        }
    }

    @PostMapping(
            path = "/auth/signin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSignInResponseDTO> signInUser(@RequestBody UserSignInRequestDTO userSignInRequestDTO) {
        String userName = userSignInRequestDTO.getUserName();
        String password = userSignInRequestDTO.getPassword();

        if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User credentials shall not be empty"
            );
        }

        User user = null;

        try {
            user = userService.findByUserName(userName);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("User with username '%s' not found", userName),
                    e
            );
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid username or password",
                    e
            );
        }

        String token = jwtTokenProvider.createToken(user.getUserName(), user.getRoles());

        UserSignInResponseDTO responseDTO = new UserSignInResponseDTO();
        responseDTO.setUserName(user.getUserName());
        responseDTO.setToken(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer_" + token)
                .body(responseDTO);
    }


}
