package dev.falae.infrastructure.adapters.services;

import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.core.domain.entities.Author;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class AuthorProfileImageService {

    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;
    private final AuthorRepository authorRepository;
    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    @Value("${upload.image.max-size-kb:600}")
    private long maxImageSizeKb;

    public AuthorProfileImageService(
            AuthenticatedAuthorProvider authenticatedAuthorProvider,
            AuthorRepository authorRepository,
            S3Client s3Client
    ) {
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
        this.authorRepository = authorRepository;
        this.s3Client = s3Client;
    }

    public String uploadProfileImage(MultipartFile file) {
        validateImageSize(file);
        validateImageFormat(file);

        AuthorEntity authorEntity = authenticatedAuthorProvider.getCurrentAuthor();
        Author author = authorRepository.findById(authorEntity.getId());

        String existingImageUrl = author.getProfileImageUrl();
        if (existingImageUrl != null && !existingImageUrl.isEmpty() && isFromMyBucket(existingImageUrl)) {
            deleteOldProfileImage(existingImageUrl);
        }

        String newImageUrl = uploadImage(file, authorEntity.getId().toString());

        author.setProfileImageUrl(newImageUrl);
        authorRepository.save(author);

        return newImageUrl;
    }

    private void validateImageSize(MultipartFile file) {
        long maxSizeBytes = maxImageSizeKb * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "O arquivo excede o tamanho máximo permitido de " + maxImageSizeKb + " KB."
            );
        }
    }

    private void validateImageFormat(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException(
                    "O arquivo está nulo. São aceitos apenas JPG/JPEG, PNG, GIF ou WEBP."
            );
        }

        String lowerFileName = fileName.toLowerCase();
        if (!lowerFileName.endsWith(".jpg") && !lowerFileName.endsWith(".jpeg") &&
            !lowerFileName.endsWith(".png") && !lowerFileName.endsWith(".gif") &&
            !lowerFileName.endsWith(".webp")) {
            throw new IllegalArgumentException(
                    "Formato não aceito. Aceito apenas JPG/JPEG, PNG, GIF e WEBP."
            );
        }
    }

    private boolean isFromMyBucket(String imageUrl) {
        return imageUrl.startsWith(publicUrl);
    }

    private void deleteOldProfileImage(String imageUrl) {
        String key = imageUrl.replace(publicUrl + "/", "");

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private String uploadImage(MultipartFile file, String authorId) {
        try {
            String ext = getFileExtension(file.getOriginalFilename());
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "profile-" + timestamp + "." + ext;
            String filePath = authorId + "/profile/" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(getImageContentType(ext))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return publicUrl + "/" + filePath;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar imagem de perfil para o S3", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return "";
    }

    private String getImageContentType(String extension) {
        if (extension == null) return "application/octet-stream";

        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
