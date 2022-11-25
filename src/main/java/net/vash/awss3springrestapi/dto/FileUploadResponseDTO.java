package net.vash.awss3springrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.vash.awss3springrestapi.model.File;

import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link File} entity
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class FileUploadResponseDTO implements Serializable {
    private String fileName;
    @PastOrPresent
    private Date eventCreated;

    public static FileUploadResponseDTO fromEntity(File file) {
        FileUploadResponseDTO dto = new FileUploadResponseDTO();

        dto.setFileName(file.getFileName());
        dto.setEventCreated(file.getEvent().getCreated());

        return dto;
    }
}