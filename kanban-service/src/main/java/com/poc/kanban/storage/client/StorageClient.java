package com.poc.kanban.storage.client;

import com.poc.kanban.storage.model.FileUploadResponse;
import com.poc.kanban.storage.model.PresignedUrlResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "storage-service",
    url = "${app.storage.url:http://localhost:8086}"
)
public interface StorageClient {

    @GetMapping("/api/storage/download/{bucket}/{path}")
    byte[] download(
        @PathVariable("bucket") String bucket,
        @PathVariable("path") String path
    );

    @PostMapping(value = "/api/storage/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileUploadResponse upload(
        @RequestPart("file") MultipartFile file,
        @RequestParam("bucket") String bucket,
        @RequestParam("path") String path,
        @RequestParam(value = "userId", required = false) UUID userId,
        @RequestParam(value = "serviceOrigin", required = false) String serviceOrigin
    );

    @DeleteMapping("/api/storage/{bucket}/{path}")
    Map<String, String> delete(
        @PathVariable("bucket") String bucket,
        @PathVariable("path") String path
    );

    @GetMapping("/api/storage/presigned/get")
    PresignedUrlResponse getPresignedUrl(
        @RequestParam("bucket") String bucket,
        @RequestParam("path") String path,
        @RequestParam(value = "expiryMinutes", defaultValue = "30") int expiryMinutes
    );

    @GetMapping("/api/storage/exists/{bucket}/{path}")
    Map<String, Boolean> exists(
        @PathVariable("bucket") String bucket,
        @PathVariable("path") String path
    );

    @PostMapping("/api/storage/bucket")
    Map<String, String> createBucket(@RequestParam("bucket") String bucket);
}
