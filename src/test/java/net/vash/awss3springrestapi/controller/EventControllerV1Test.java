package net.vash.awss3springrestapi.controller;

import lombok.SneakyThrows;
import net.vash.awss3springrestapi.dto.UserEventsResponseDTO;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html
* */

@SpringBootTest
@ComponentScan("net.vash.awss3springrestapi")
class EventControllerV1Test {
//    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private EventControllerV1 eventControllerV1;
    @Autowired
    SecurityFilterChain filterChain;

    private List<Event> events;
    private UserEventsResponseDTO userEventsResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        events = List.of(
                new Event(1L, EventType.CREATED, new Date(), null, new File(1L, "f1","f1", null)),
                new Event(2L, EventType.DOWNLOADED, new Date(), null, new File(2L, "f1","f1", null)),
                new Event(3L, EventType.DELETED, new Date(), null, new File(3L, "f1","f1", null)),
                new Event(4L, EventType.DOWNLOADED, new Date(), null, new File(4L, "f2","f2", null))
        );
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "some name",roles = {"BOBIK"})
    void getEventsByUserNameForbiddenForBadRole() {
        mockMvc
                .perform(
                        get("/api/v1/events/someName")
                )
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        ;
        verifyNoInteractions(eventService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void getEventsByUserNameIsUnauthorized() {
        mockMvc
                .perform(
                        get("/api/v1/events/someName")
                )
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        ;
        verifyNoInteractions(eventService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "just_user",roles="ADMINISTRATOR")
    void getEventsByUserNameSuccessfulWhenUserIsAdmin() {
        when(eventService.getEventsByUserName(anyString())).thenReturn(events);

        mockMvc
                .perform(
                        get("/api/v1/events/someName")
                )
                .andExpect(status().is(HttpStatus.OK.value()))
        ;

        verify(eventService, times(1)).getEventsByUserName(any());
        verifyNoMoreInteractions(eventService);
    }
}