package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.Role;
import net.vash.awss3springrestapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Long> {

    User findByUserNameIgnoreCase(@NonNull String userName);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

}
