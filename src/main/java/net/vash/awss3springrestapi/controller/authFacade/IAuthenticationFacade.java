package net.vash.awss3springrestapi.controller.authFacade;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuth();
}
