package net.vash.awss3springrestapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.vash.awss3springrestapi.dto.UserEventsResponseDTO;
import net.vash.awss3springrestapi.dto.UserSignInResponseDTO;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.service.EventService;
import net.vash.awss3springrestapi.service.exceptions.EventRepositoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(
        path = "/api/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EventControllerV1 {

    private final EventService eventService;

    @GetMapping(
            path = "/events/{userName}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserEventsResponseDTO> getEventsByUserName(@PathVariable String userName) {
        List<Event> eventList = null;

        try {
            eventList = eventService.getEventsByUserName(userName);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "UserName shall not be null"
            );
        } catch (EventRepositoryException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something goes wrong with a datalayer"
            );
        }

        List<UserEventsResponseDTO.EventDto> eventsDTO = new ArrayList<>();
        if (eventList != null && !eventList.isEmpty()) {
            eventList.forEach(
                    event -> {
                        eventsDTO.add(UserEventsResponseDTO.EventDto.fromEntity(event));
                    }
            );
        }

        UserEventsResponseDTO userEventsResponseDTO = new UserEventsResponseDTO(
                userName,
                eventsDTO
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userEventsResponseDTO);
    }
}
