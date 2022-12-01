package net.vash.awss3springrestapi.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class S3StorageFileDownloadException extends RuntimeException{
}
