package net.vash.awss3springrestapi.service.impl;

import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.repository.EventRepo;
import net.vash.awss3springrestapi.service.EventService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {EventServiceImpl.class})
class EventServiceImplTest {

    @MockBean
    private EventRepo eventRepo;

    @Autowired
    private EventService eventService;

    @Mock
    private static List<Event> events;

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    void getEventsByUserNameThrowsExceptionWhenNullArgumentPassed() {
        assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByUserName(null));
        verifyNoInteractions(eventRepo);
    }

    @Test
    void getEventsByUserNameRepoThrowsException() {
        when(eventRepo.findByUser_UserNameIgnoreCase(anyString())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> eventService.getEventsByUserName("some user name"));

        verify(eventRepo, times(1)).findByUser_UserNameIgnoreCase(anyString());
        verifyNoMoreInteractions(eventRepo);
    }

    @Test
    void getEventsByUserName() {
        int size = 10;
        events = (List<Event>) mock(List.class);
        when(events.size()).thenReturn(size);
        when(eventRepo.findByUser_UserNameIgnoreCase(anyString())).thenReturn(events);

        List<Event> returnedCollection = eventService.getEventsByUserName(anyString());

        assertSame(returnedCollection, events);

        verify(eventRepo, times(1)).findByUser_UserNameIgnoreCase(anyString());
        verifyNoMoreInteractions(eventRepo);
    }
}