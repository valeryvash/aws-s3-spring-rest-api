package net.vash.awss3springrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;

import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link net.vash.awss3springrestapi.model.File} entity
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDeleteResponseDTO implements Serializable {
    private Long id;
    private String fileName;
    private String filePath;
    private EventType eventEventType;
    @PastOrPresent
    private Date eventCreated;

    public static FileDeleteResponseDTO fromEntity(File file) {
        FileDeleteResponseDTO dto = new FileDeleteResponseDTO();

        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFilePath(file.getFilePath());
        dto.setEventEventType(file.getEvent().getEventType());
        dto.setEventCreated(file.getEvent().getCreated());

        return dto;
    }
}