package net.vash.awss3springrestapi.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

public class JwtUser implements UserDetails {

    private final Long id;
    private final boolean enabled;
    private final Date lastPasswordResetDate;

    private final String username;
    private final String password;

    private final String firstName;
    private final String lastName;
    private final String email;

    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUser(
            Long id,
            boolean enabled,
            Date lastPasswordResetDate,

            String username,
            String password,

            String firstName,
            String lastName,
            String email,

            Collection<? extends GrantedAuthority> authorities) {

        this.id = id;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;

        this.username = username;
        this.password = password;

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        this.authorities = authorities;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public Long id() {
        return id;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
