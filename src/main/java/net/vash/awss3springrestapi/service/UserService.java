package net.vash.awss3springrestapi.service;

import net.vash.awss3springrestapi.model.User;

public interface UserService {

    User findByUserName(String userName);

    User signUp(User user);

    User update(User user);

    User deleteByUserName(String userName);

    boolean isUserExistByUserNameIgnoreCase(String userName);

    boolean isUserExistByEmailIgnoreCase(String email);


}
