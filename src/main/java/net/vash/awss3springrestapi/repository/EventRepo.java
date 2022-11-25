package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EventRepo extends CrudRepository<Event,Long> {
    @Query("select e from Event e where upper(e.user.userName) = upper(?1)")
    List<Event> findByUser_UserNameIgnoreCase(@NonNull String userName);
}
