package net.vash.awss3springrestapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.security.jwt.JwtUser;
import net.vash.awss3springrestapi.security.jwt.JwtUserFactory;
import net.vash.awss3springrestapi.exceptions.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User loadedUser = userRepo.findByUserName(username);

        if (loadedUser == null) {
            log.warn("IN loadUserByUsername method user not found by userName: {}", username);
            throw new UserNotFoundException();
        }

        log.info("IN loadUserByUsername method user successfully found. User name : {}", username);

        JwtUser jwtUser = JwtUserFactory.create(loadedUser);

        log.info("IN loadUserByUsername method user successfully converted to JwtUser. User name : {}", username);

        return jwtUser;
    }

}
