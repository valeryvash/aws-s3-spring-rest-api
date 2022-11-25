package net.vash.awss3springrestapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserSignInResponseDTO implements Serializable {
    private String userName;
    private String token;
}
