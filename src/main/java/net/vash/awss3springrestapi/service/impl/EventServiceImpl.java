package net.vash.awss3springrestapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.repository.EventRepo;
import net.vash.awss3springrestapi.service.EventService;
import net.vash.awss3springrestapi.service.exceptions.EventRepositoryException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;

    public EventServiceImpl(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    @Override
    public List<Event> getEventsByUserName(String userName) {
        if (userName == null) {
            log.warn("IN getEventsByUserName method passed null argument");
            throw new IllegalArgumentException();
        }

        try {
            List<Event> events = eventRepo.findByUser_UserName(userName);
            log.info("IN getEventsByUserName method events found successfully. Collection size: {}", events.size());
            return events;
        } catch (RuntimeException e) {
            log.warn("IN getEventsByUserName method event repository exception occurred. User name: {} ", userName );
            throw new EventRepositoryException();
        }
    }
}
