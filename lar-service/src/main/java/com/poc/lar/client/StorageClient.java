package com.poc.lar.client;

import com.poc.lar.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(
    name = "storage-service",
    url = "${app.storage.url}",
    configuration = FeignConfig.class
)
public interface StorageClient {

    @PostMapping(value = "/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> uploadFile(
        @RequestPart("file") MultipartFile file,
        @RequestPart("bucket") String bucket,
        @RequestPart(value = "folder", required = false) String folder
    );

    @DeleteMapping("/api/files/{fileId}")
    void deleteFile(@PathVariable("fileId") String fileId);

    @GetMapping("/api/files/{fileId}/url")
    Map<String, Object> getFileUrl(@PathVariable("fileId") String fileId);
}
