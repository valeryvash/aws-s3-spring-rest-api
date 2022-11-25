package net.vash.awss3springrestapi.service;

import net.vash.awss3springrestapi.model.Event;

import java.util.List;

public interface EventService {
    List<Event> getEventsByUserName(String userName);
}
