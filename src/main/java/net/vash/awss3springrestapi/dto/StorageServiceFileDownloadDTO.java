package net.vash.awss3springrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class StorageServiceFileDownloadDTO {
    private final byte[] data;
    private final String fileName;

}
