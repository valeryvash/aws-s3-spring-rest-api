package net.vash.awss3springrestapi.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Separated component. The goal is to resolve the circular dependency:
 * JwtTokenProvider-> UserServiceImpl-> JwtUserDetailsService
 */

@Component
public class BcryptPasswordEncoderProvider {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
