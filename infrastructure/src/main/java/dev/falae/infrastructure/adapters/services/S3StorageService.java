package dev.falae.infrastructure.adapters.services;

import dev.falae.application.ports.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void deleteFolder(String folderPath) {
        String prefix = folderPath.endsWith("/") ? folderPath : folderPath + "/";

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        List<S3Object> objects = listResponse.contents();

        if (objects.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = objects.stream()
                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                .toList();

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objectsToDelete).build())
                .build();

        s3Client.deleteObjects(deleteRequest);
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = filePaths.stream()
                .map(this::extractKeyFromUrl)
                .filter(key -> key != null && !key.isEmpty())
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        if (objectsToDelete.isEmpty()) {
            return;
        }

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objectsToDelete).build())
                .build();

        s3Client.deleteObjects(deleteRequest);
    }

    private String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        int bucketIndex = url.indexOf(bucketName);
        if (bucketIndex == -1) {
            return url;
        }
        int keyStart = url.indexOf("/", bucketIndex + bucketName.length());
        if (keyStart == -1) {
            return null;
        }
        return url.substring(keyStart + 1);
    }
}
