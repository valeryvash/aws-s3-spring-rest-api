package net.vash.awss3springrestapi.controller;

import lombok.SneakyThrows;
import net.vash.awss3springrestapi.dto.UserEventsResponseDTO;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html
* */

//@SpringBootTest(classes = {EventControllerV1.class})
@WebMvcTest(
        controllers = {EventControllerV1.class}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventControllerV1Test {
    private static final String API_VERSION_MAPPING = "/api/v1/admin";

    private static final String GET_USER_EVENTS_MAPPING = API_VERSION_MAPPING + "/events";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private EventControllerV1 eventControllerV1;
    private List<Event> events;
    private UserEventsResponseDTO userEventsResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        events = List.of(
                new Event(1L, EventType.CREATED, new Date(), null, new File(1L, "f1", null)),
                new Event(2L, EventType.DOWNLOADED, new Date(), null, new File(2L, "f1", null)),
                new Event(3L, EventType.DELETED, new Date(), null, new File(3L, "f1", null)),
                new Event(4L, EventType.DOWNLOADED, new Date(), null, new File(4L, "f2", null))
        );
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "some name",roles = {"BOBIK"})
    void getEventsByUserNameTest() {
        mockMvc
                .perform(
                        get("/api/v1/admin/events")
                )
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        ;
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void getEventsByUserNameIsUnauthorized() {
//        mockMvc
//                .perform(
//                        get(GET_USER_EVENTS_MAPPING + "/someName")
//                )
//                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
//        ;
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "just_user",roles="USER")
    void getEventsByUserName() {
//        when(eventService.getEventsByUserName(anyString())).thenReturn(events);
//
//        mockMvc
//                .perform(
//                        get(GET_USER_EVENTS_MAPPING + "/someName")
//                )
//                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
//        ;
//
//        verify(eventService, times(1)).getEventsByUserName(any());
//        verifyNoMoreInteractions(eventService);
    }
}