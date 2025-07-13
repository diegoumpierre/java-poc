package br.dev.service;


import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioUploader {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioUploader(@Value("${minio.url}") String url,
                         @Value("${minio.access-key}") String accessKey,
                         @Value("${minio.secret-key}") String secretKey,
                         @Value("${minio.bucket}") String bucket) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
    }

    public void upload(String filename, InputStream input, long size) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(input, size, -1)
                        .contentType("application/octet-stream")
                        .build()
        );
    }
}
