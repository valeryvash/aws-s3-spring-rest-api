package net.vash.awss3springrestapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.model.Role;
import net.vash.awss3springrestapi.model.User;
import net.vash.awss3springrestapi.repository.RoleRepo;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.service.UserService;
import net.vash.awss3springrestapi.service.exceptions.FieldsAlreadyExistException;
import net.vash.awss3springrestapi.service.exceptions.UserDeleteByUserNameException;
import net.vash.awss3springrestapi.service.exceptions.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE_NAME = "ROLE_USER";
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByUserName(String userName) {
        User user = userRepo.findByUserNameIgnoreCase(userName);

        if (user == null) {
            log.warn("IN findByUserName method user not found by username : {}", userName);
            throw new UserNotFoundException();
        }

        log.info("IN findByUsername method user found successfully. Username: {}", userName);

        return user;
    }

    @Override
    public User signUp(User user) {

        if (user == null) {
            log.warn("IN signUp method null user passed");
            throw new IllegalArgumentException();
        }

        Role userRole = roleRepo.findByRoleNameIgnoreCase(DEFAULT_ROLE_NAME);

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.getRoles().add(userRole);

        try {
            user = userRepo.save(user);
        } catch (RuntimeException e) {
            log.warn("IN signUp method some field(s) already exist in database.Try another.");
            throw new FieldsAlreadyExistException();
        }

        log.info("IN signUp method user saved successfully. Username: {}", user.getUserName());

        return user;
    }

    @Override
    public User update(User user) {

        if (user == null) {
            log.warn("IN signUp method null user passed");
            throw new IllegalArgumentException();
        }

        User userToBeUpdated = userRepo.findByUserNameIgnoreCase(user.getUserName());

        if (userToBeUpdated == null) {
            log.warn("IN update method user not found by username : {}", user.getUserName());
            throw new UserNotFoundException();
        }

        userToBeUpdated.setFirstName(user.getFirstName());
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setEmail(user.getEmail());

        try {
            userToBeUpdated = userRepo.save(userToBeUpdated);
        } catch (RuntimeException e) {
            log.warn("IN update method some field(s) already exist in database.Try another.");
            throw new FieldsAlreadyExistException();
        }

        log.info("IN update method user successfully updated. Username: {}", userToBeUpdated.getUserName());

        return userToBeUpdated;
    }

    @Override
    public User deleteByUserName(String userName) {
        User userToBeDeleted = userRepo.findByUserNameIgnoreCase(userName);

        if (userToBeDeleted == null) {
            log.warn("IN deleteByUserName method user not found by username : {}", userName);
            throw new UserNotFoundException();
        }

        try {
            userRepo.delete(userToBeDeleted);
        } catch (RuntimeException e) {
            log.warn("IN deleteByUserName method operation exception occurred. Username: {} ", userName);
            throw new UserDeleteByUserNameException();
        }

        log.info("IN deleteByUserName user successfully deleted. Username: {}", userName);

        return userToBeDeleted;
    }

    public boolean isUserExistByUserNameIgnoreCase(String userName) {
        boolean result = userRepo.existsByUserNameIgnoreCase(userName);

        log.info("IN isUserExistByUserNameIgnoreCase method result: {}", result);

        return result;
    }

    public boolean isUserExistByEmailIgnoreCase(String email) {
        boolean result = userRepo.existsByEmailIgnoreCase(email);

        log.info("IN isUserExistByUserNameIgnoreCase method result: {}", result);

        return result;
    }

}
