package net.vash.awss3springrestapi.controller;

import lombok.RequiredArgsConstructor;
import net.vash.awss3springrestapi.dto.FileDeleteResponseDTO;
import net.vash.awss3springrestapi.dto.FileUploadResponseDTO;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.S3Storage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkException;

@RestController
@RequestMapping(
        path = "/api/v1/files"
)
@RequiredArgsConstructor
public class S3StorageControllerV1 {

    private final S3Storage s3Storage;

    @PostMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole(MODERATOR)")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("filePath") String filePath,
            Authentication authentication
            ) {
        String userName = authentication.getName();

        File uploadFileForUser = s3Storage.uploadFileForUser(multipartFile, filePath, userName);

        FileUploadResponseDTO responseDTO = FileUploadResponseDTO.fromEntity(uploadFileForUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }


    @GetMapping(
            path = "/{fileId}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @PreAuthorize("hasRole(USER)")
    ResponseEntity<ByteArrayResource> downloadFileById(
            @PathVariable("fileId") String fileId,
            Authentication authentication
    ) {

        String userName = authentication.getName();

        long fileIdValue = Long.parseLong(fileId);

        ByteArrayResource byteArrayResource = new ByteArrayResource(s3Storage.downloadFileById(fileIdValue, userName));

        return ResponseEntity
                .status(200)
                .contentLength(byteArrayResource.contentLength())
                .header("Content-type","application/octet-stream")
                .header("Content-disposition", "attachment; fileId=\"" + fileId + "\"")
                .body(byteArrayResource);
    }

    @DeleteMapping(
            path = "/{fileId}"
    )
    @PreAuthorize("hasRole(MODERATOR)")
    public ResponseEntity<FileDeleteResponseDTO> deleteFileById(
            @PathVariable("fileId") String fileId,
            Authentication authentication
    ) {
        String userName = authentication.getName();

        long fileIdValue = Long.parseLong(fileId);

        File deletedFileInfo = s3Storage.deleteFileById(fileIdValue, userName);

        FileDeleteResponseDTO dto = FileDeleteResponseDTO.fromEntity(deletedFileInfo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(dto);
    }

    @ExceptionHandler(NumberFormatException.class)
    public void handleNumberFormatException(NumberFormatException e) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Passed parameter shall be a long number",
                e.getCause()
        );
    }

    @ExceptionHandler(SdkException.class)
    public void handleSdkException(SdkException e) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error occurred during storage manipulation",
                e.getCause()
        );
    }

    @ExceptionHandler(MultipartException.class)
    public void handleMultipartException(MultipartException e) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Error occurred during file upload",
                e.getCause()
        );
    }

}