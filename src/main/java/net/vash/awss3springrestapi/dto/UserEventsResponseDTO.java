package net.vash.awss3springrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.vash.awss3springrestapi.model.Event;
import net.vash.awss3springrestapi.model.EventType;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A DTO for the {@link net.vash.awss3springrestapi.model.User} entity
 */
@AllArgsConstructor
@Getter
public class UserEventsResponseDTO implements Serializable {

    private final String userName;
    private final List<EventDto> events;

    /**
     * A DTO for the {@link net.vash.awss3springrestapi.model.Event} entity
     */
    @AllArgsConstructor
    @Getter
    public static class EventDto implements Serializable {
        private final EventType eventType;
        @PastOrPresent
        private final Date created;
//        @NotNull
//        @NotEmpty
//        @Min(value = 3, message = "File name shall contain 3 symbols at least")
//        @Max(value = 255, message = "File name shall not contain more than 255 symbols")
        private final String fileFileName;

        public static EventDto fromEntity(Event event) {
            return new EventDto(
                    event.getEventType(),
                    event.getCreated(),
                    event.getFile().getFileName()
            );
        }

    }
}