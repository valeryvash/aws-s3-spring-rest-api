package net.vash.awss3springrestapi.config;

import net.vash.awss3springrestapi.security.jwt.JwtConfigurer;
import net.vash.awss3springrestapi.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    private static final String MODERATOR_ENDPOINT = "/api/v1/moder/**";
    private static final String SIGNUP_ENDPOINT = "/api/v1/auth/signup/";
    private static final String SIGNIN_ENDPOINT = "/api/v1/auth/signin/";

    private static final String ADMIN_ROLE = "ADMINISTRATOR";
    private static final String MODER_ROLE = "MODERATOR";
    private static final String USER_ROLE = "USER";

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers(ADMIN_ENDPOINT).hasRole(ADMIN_ROLE)
//                .antMatchers(SIGNUP_ENDPOINT).hasRole(ADMIN_ROLE)
//                .antMatchers(SIGNIN_ENDPOINT).permitAll()
//                .antMatchers(HttpMethod.POST, MODERATOR_ENDPOINT).hasRole(MODER_ROLE)
//                .antMatchers(HttpMethod.DELETE, MODERATOR_ENDPOINT).hasRole(MODER_ROLE)
//                .antMatchers(HttpMethod.GET, MODERATOR_ENDPOINT).hasRole(USER_ROLE)
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .apply(new JwtConfigurer(jwtTokenProvider))
//                ;
//
//        return http.build();
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
                http
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(ADMIN_ENDPOINT).hasRole(ADMIN_ROLE)
                .antMatchers(SIGNUP_ENDPOINT).hasRole(ADMIN_ROLE)
                .antMatchers(SIGNIN_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.POST, MODERATOR_ENDPOINT).hasRole(MODER_ROLE)
                .antMatchers(HttpMethod.DELETE, MODERATOR_ENDPOINT).hasRole(MODER_ROLE)
                .antMatchers(HttpMethod.GET, MODERATOR_ENDPOINT).hasRole(USER_ROLE)
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                ;
    }
}
