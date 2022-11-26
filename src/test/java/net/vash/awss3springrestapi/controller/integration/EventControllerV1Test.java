package net.vash.awss3springrestapi.controller.integration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.vash.awss3springrestapi.controller.EventControllerV1;
import net.vash.awss3springrestapi.repository.FileRepo;
import net.vash.awss3springrestapi.repository.RoleRepo;
import net.vash.awss3springrestapi.repository.UserRepo;
import net.vash.awss3springrestapi.security.jwt.JwtTokenProvider;
import net.vash.awss3springrestapi.service.EventService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.Filter;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventControllerV1.class)
@ComponentScan("net.vash.awss3springrestapi.security")
public class EventControllerV1Test {

//    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    EventService eventService;
    @MockBean
    UserRepo userRepo;

    SecurityFilterChain filterChain;


    @BeforeEach
    public void setUp() throws Exception {
//        Filter[] filters = filterChain.getFilters().toArray(new Filter[filterChain.getFilters().size()]);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
//                .addFilters(filters)
                .apply(springSecurity())
                .build();

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

}