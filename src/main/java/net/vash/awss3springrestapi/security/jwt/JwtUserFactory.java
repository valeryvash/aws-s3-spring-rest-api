package net.vash.awss3springrestapi.security.jwt;

import net.vash.awss3springrestapi.model.Role;
import net.vash.awss3springrestapi.model.Status;
import net.vash.awss3springrestapi.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {

    private static final Status ACTIVE_STATUS = Status.ACTIVE;
    private JwtUserFactory() {}

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getStatus().equals(ACTIVE_STATUS),
                user.getUpdated(),

                user.getUserName(),
                user.getPassword(),

                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),

                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles
                .stream()
                .map( role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }
}
