package net.vash.awss3springrestapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.vash.awss3springrestapi.model.User;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpResponseDTO implements Serializable {

    private Long id;
    private String userName;

    private String firstName;
    private String lastName;
    private String email;

    public static UserSignUpResponseDTO fromUser(User user) {
        UserSignUpResponseDTO userSignUpResponseDTO = new UserSignUpResponseDTO();

        userSignUpResponseDTO.setId(user.getId());
        userSignUpResponseDTO.setUserName(user.getUserName());

        userSignUpResponseDTO.setFirstName(user.getFirstName());
        userSignUpResponseDTO.setLastName(user.getLastName());
        userSignUpResponseDTO.setEmail(user.getEmail());

        return userSignUpResponseDTO;
    }

    public User fromDTO() {
        User user = new User();

        user.setId(this.getId());
        user.setUserName(this.getUserName());

        user.setFirstName(this.getFirstName());
        user.setLastName(this.getLastName());
        user.setEmail(this.getEmail());

        return user;
    }
}
