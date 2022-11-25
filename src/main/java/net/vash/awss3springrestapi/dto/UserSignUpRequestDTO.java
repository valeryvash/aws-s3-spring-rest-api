package net.vash.awss3springrestapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.vash.awss3springrestapi.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequestDTO implements Serializable {
    private String userName;
    private String password;

    private String firstName;
    private String lastName;
    private String email;

    public static UserSignUpRequestDTO fromUser(User user) {
        UserSignUpRequestDTO userToBeReturned = new UserSignUpRequestDTO();

        userToBeReturned.setUserName(user.getUserName());
        userToBeReturned.setPassword(user.getPassword());

        userToBeReturned.setFirstName(user.getFirstName());
        userToBeReturned.setLastName(user.getLastName());
        userToBeReturned.setEmail(user.getEmail());

        return userToBeReturned;
    }

    public User fromDTO() {
        User user = new User();

        user.setUserName(this.getUserName());
        user.setPassword(this.getPassword());

        user.setFirstName(this.getFirstName());
        user.setLastName(this.getLastName());
        user.setEmail(this.getEmail());

        return user;
    }
}
