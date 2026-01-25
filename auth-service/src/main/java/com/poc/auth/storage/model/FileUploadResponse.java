package com.poc.auth.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private boolean success;
    private String message;
    private UUID fileId;
    private String objectName;
    private String url;
    private String bucket;
    private long size;
    private String contentType;
}
