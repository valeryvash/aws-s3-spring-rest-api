package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface RoleRepo extends CrudRepository<Role,Long> {
    @NonNull
    Role findByRoleName(@NonNull String roleName);

    @Query("select r from Role r inner join r.users users where upper(users.userName) = upper(?1)")
    List<Role> findRolesByUserName(@NonNull String userName);
}
