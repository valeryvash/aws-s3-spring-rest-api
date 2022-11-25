package net.vash.awss3springrestapi.controller;

import net.vash.awss3springrestapi.controller.authFacade.AuthFacade;
import net.vash.awss3springrestapi.dto.FileUploadResponseDTO;
import net.vash.awss3springrestapi.model.EventType;
import net.vash.awss3springrestapi.model.File;
import net.vash.awss3springrestapi.service.FileService;
import net.vash.awss3springrestapi.service.StorageService;
import net.vash.awss3springrestapi.service.exceptions.FileSaveException;
import net.vash.awss3springrestapi.service.exceptions.UserNotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(
        path = "/api/v1/files"
)
public class FileControllerV1 {

    private final FileService fileService;
    private final StorageService storageService;
    private final AuthFacade authFacade;

    public FileControllerV1(FileService fileService, StorageService storageService, AuthFacade authFacade) {
        this.fileService = fileService;
        this.storageService = storageService;
        this.authFacade = authFacade;
    }

    @PostMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName
    ) {

        if (file == null || file.isEmpty() ||
            fileName == null || fileName.isEmpty() || fileName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File or fileName shall not be null or empty"
            );
        }

        String userName = getLoggedUsernameOrThrownException();

        boolean isFileUploadedSuccessfully = storageService.uploadFile(file, fileName);

        if (!isFileUploadedSuccessfully) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "File upload error"
            );
        }

        File fileInfo = new File();
        fileInfo.setFileName(fileName);

        fileInfo = updateFileServiceOrThrownException(fileInfo, userName);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FileUploadResponseDTO.fromEntity(fileInfo));

    }

    @GetMapping(
            path = "/{fileName}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileName") String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File name shall not be null/empty/blank"
            );
        }

        byte[] file = storageService.getFileByFileName(fileName);

        if (file.length == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Requested file not found"
            );
        }

        String userName = getLoggedUsernameOrThrownException();

        File downloadedFileInfo = new File();

        downloadedFileInfo.setFileName(fileName);
        downloadedFileInfo.getEvent().setEventType(EventType.DOWNLOADED);

        updateFileServiceOrThrownException(downloadedFileInfo, userName);

        ByteArrayResource byteArrayResource = new ByteArrayResource(file);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(byteArrayResource.contentLength())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayResource);
    }

    @DeleteMapping(
            path = "/{fileName}"
    )
    public ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File name shall not be null/empty/blank"
            );
        }

        String userName = getLoggedUsernameOrThrownException();

        boolean isFileNotDeletedSuccessfully = !storageService.deleteFileByFileName(fileName);

        if (isFileNotDeletedSuccessfully) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "File was not deleted successfully"
            );
        }

        File deletedFileInfo = new File();

        deletedFileInfo.setFileName(fileName);
        deletedFileInfo.getEvent().setEventType(EventType.DELETED);

        updateFileServiceOrThrownException(deletedFileInfo, userName);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(String.format("File with filename %s deleted successfully", fileName));
    }

    private String getLoggedUsernameOrThrownException() {
        try {
            return authFacade.getAuth().getName();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authorization required"
            );
        }
    }

    private File updateFileServiceOrThrownException(File fileInfo, String userName) {
        try {
            return fileService.addFileForUserByUserName(fileInfo, userName);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bad data provided"
            );
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Logged user not found in database"
            );
        } catch (FileSaveException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred during file credential persist"
            );
        }
    }


}
