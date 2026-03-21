package dev.falae.infrastructure.adapters.services;

import dev.falae.application.ports.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.List;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${r2.bucket}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    @Value("${r2.presigned-url-expiration-minutes:60}")
    private int presignedUrlExpirationMinutes;

    public S3StorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
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

    public String generatePresignedUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        String key = extractKeyFromPublicUrl(fileUrl);
        if (key == null || key.isEmpty()) {
            return fileUrl;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlExpirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    private String extractKeyFromPublicUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (url.startsWith(publicUrl)) {
            String path = url.substring(publicUrl.length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        }
        return extractKeyFromUrl(url);
    }

    public String moveFileToPublicPath(String privateUrl) {
        if (privateUrl == null || privateUrl.isEmpty()) {
            return null;
        }

        String sourceKey = extractKeyFromPublicUrl(privateUrl);
        if (sourceKey == null || !sourceKey.contains("/private/")) {
            return privateUrl;
        }

        String destinationKey = sourceKey.replace("/private/", "/");

        s3Client.copyObject(builder -> builder
                .sourceBucket(bucketName)
                .sourceKey(sourceKey)
                .destinationBucket(bucketName)
                .destinationKey(destinationKey)
        );

        s3Client.deleteObject(builder -> builder
                .bucket(bucketName)
                .key(sourceKey)
        );

        return publicUrl + "/" + destinationKey;
    }
}
