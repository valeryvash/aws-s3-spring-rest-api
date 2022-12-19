package net.vash.awss3springrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.model.EventType;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserEventsResponseDTO implements Serializable {

    private String userName;
    private List<EventDto> events;

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class EventDto implements Serializable {
        private EventType eventType;
        private Date created;
        private String fileFileName;

        public static EventDto fromEntity(Event event) {
            return new EventDto(
                    event.getEventType(),
                    event.getCreated(),
                    event.getFile().getFileName()
            );
        }

    }
}