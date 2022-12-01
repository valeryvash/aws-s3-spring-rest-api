package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface UserRepo extends JpaRepository<User,Long> {

    User findByUserName(@NonNull String userName);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

}
