package net.vash.awss3springrestapi.controller.authFacade;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade implements IAuthenticationFacade {
    @Override
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
