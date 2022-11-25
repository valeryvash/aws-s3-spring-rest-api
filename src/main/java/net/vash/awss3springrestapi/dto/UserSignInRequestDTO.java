package net.vash.awss3springrestapi.dto;

import lombok.*;
import net.vash.awss3springrestapi.model.User;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInRequestDTO implements Serializable {
    private String userName;
    private String password;

    public static UserSignInRequestDTO fromUser(User user) {
        UserSignInRequestDTO requestDTO = new UserSignInRequestDTO();

        requestDTO.setUserName(user.getUserName());
        requestDTO.setPassword(user.getPassword());

        return requestDTO;
    }

    public User fromDTO(UserSignInRequestDTO responseDTO) {
        User result = new User();

        result.setUserName(this.getUserName());
        result.setPassword(this.password);

        return result;
    }
}
