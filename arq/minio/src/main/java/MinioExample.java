import io.minio.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class MinioExample {
    public static void main(String[] args) throws Exception {
        // 1. Conexão com o MinIO
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.0.10:9000")
                .credentials("minioadmin", "minioadmin")
                .build();

        String bucketName = "test-bucket";
        String filePath = "/Users/diegoumpierre/Documents/git_diego_umpierre/poc/arq/minio/src/main/java/example.txt"; // arquivo local
        String objectName = "uploaded-example.txt"; // nome que será salvo no MinIO

        // 2. Criar o bucket se não existir
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println("Bucket created: " + bucketName);
        }

        // 3. Upload do arquivo
        try (InputStream inputStream = new FileInputStream(filePath)) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, -1, 10485760) // tamanho -1 para tamanho desconhecido, limite 10MB por parte
                    .contentType("application/octet-stream")
                    .build()
            );
            System.out.println("Upload completed: " + objectName);
        }

        // 4. Download do arquivo
        minioClient.downloadObject(
            DownloadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename("downloaded-" + objectName)
                .build()
        );
        System.out.println("Download completed: downloaded-" + objectName);
    }
}