package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EventRepo extends Repository<Event,Long> {
    @Query("select e from Event e where upper(e.user.userName) = upper(?1)")
    List<Event> findByUser_UserName(@NonNull String userName);
}
