package dev.falae.infrastructure.adapters.services;

import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.core.domain.entities.Article;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ContentArticleService {

    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;
    private final ArticleRepository articleRepository;
    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    @Value("${upload.image.max-size-kb:600}")
    private long maxImageSizeKb;

    public ContentArticleService(
            AuthenticatedAuthorProvider authenticatedAuthorProvider,
            ArticleRepository articleRepository,
            S3Client s3Client
    ) {
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
        this.articleRepository = articleRepository;
        this.s3Client = s3Client;
    }

    public String linkContentToArticle(MultipartFile file, UUID articleId) {

        Article article = articleRepository.findById(articleId);
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException(
                    "O arquivo está nulo. São aceitos apenas .md ou .html."
            );
        }

        if (!fileName.endsWith(".md") && !fileName.endsWith(".html")) {
            throw new IllegalArgumentException(
                    "Formato não aceito. Aceito apenas .md ou .html."
            );
        }

        String existingContentUrl = article.getUrlArticleContent();
        if (existingContentUrl != null && !existingContentUrl.isEmpty()) {
            deleteOldContentFiles(article);
        }

        if (fileName.endsWith(".md")){
            article.setMarkdown(true);
        }

        if (fileName.endsWith(".html")){
            article.setMarkdown(false);
        }

        String contentArticlePath = processArticleContentFile(file, article);
        setterContentArticle(article, contentArticlePath);
        return contentArticlePath;
    }

    private void deleteOldContentFiles(Article article) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        String baseFileName = article.getSlug();
        String basePath = author.getId() + "/" + article.getId() + "/" + baseFileName;

        List<String> possibleKeys = List.of(
                basePath + ".md",
                basePath + ".html"
        );

        deleteByKeys(possibleKeys);
    }

    private void deleteByKeys(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = keys.stream()
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

    public String linkCoverToArticle(MultipartFile file, UUID articleId) {
        validateImageSize(file);

        Article article = articleRepository.findById(articleId);
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException(
                    "O arquivo está nulo. São aceitos apenas JPG/JPEG, PNG, GIF ou WEBP."
            );
        }

        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png") && !fileName.endsWith(".gif") && !fileName.endsWith(".webp")) {
            throw new IllegalArgumentException(
                    "Formato não aceito. Aceito apenas JPG/JPEG, PNG, GIF e WEBP."
            );
        }

        String contentArticlePath = processArticleImage(file, article);
        setterArticleCover(article, contentArticlePath);
        return contentArticlePath;
    }

    private String processArticleContentFile(MultipartFile file, Article article) {

        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        String fileUrl;

        try {
            UUID authorId = author.getId();

            String ext = getFileExtension(file.getOriginalFilename());
            if (!ext.equals("html") && !ext.equals("md")) {
                throw new IllegalArgumentException("Somente .html e .md são permitidos");
            }

            String fileName = article.getSlug() + "." + ext;
            String filePath = authorId + "/" + article.getId() + "/" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(getContentType(ext))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            fileUrl = publicUrl + "/" + filePath;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar arquivo para o S3", e);
        }

        return fileUrl;
    }

    public String linkImageToArticle(MultipartFile file, UUID articleId) {
        validateImageSize(file);

        Article article = articleRepository.findById(articleId);
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException(
                    "O arquivo está nulo. São aceitos apenas JPG/JPEG, PNG, GIF ou WEBP."
            );
        }

        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png") && !fileName.endsWith(".gif") && !fileName.endsWith(".webp")) {
            throw new IllegalArgumentException(
                    "Formato não aceito. Aceito apenas JPG/JPEG, PNG, GIF e WEBP."
            );
        }

        String contentArticlePath = processArticleImage(file, article);
        addArticleImage(article, contentArticlePath);
        return contentArticlePath;
    }

    private String getContentType(String extension) {
        return switch (extension) {
            case "html" -> "text/html";
            case "md" -> "text/markdown";
            default -> "application/octet-stream";
        };
    }

    private String getImageType(String extension) {
        if (extension == null) return "application/octet-stream";

        return switch (extension.toLowerCase()) {
            case "html" -> "text/html";
            case "md" -> "text/markdown";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private String processArticleImage(MultipartFile file, Article article) {

        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        String fileUrl;
        try {
            String ext = getFileExtension(file.getOriginalFilename());
            if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("gif") && !ext.equals("webp")) {
                throw new IllegalArgumentException("Somente imagens do tipo: JPG/JPEG, PNG, GIF e WEBP são permitidos");
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = timestamp + "." + ext;
            String filePath = author.getId() + "/" + article.getId() + "/" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(getImageType(ext))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            fileUrl = publicUrl + "/" + filePath;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar arquivo para o S3", e);
        }

        return fileUrl;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return "";
    }

    private void setterContentArticle(Article article, String contentArticlePath) {
        articleRepository.saveArticleContent(article, contentArticlePath);
    }

    private void setterArticleCover(Article article, String articleCoverPath) {
        articleRepository.saveArticleCover(article, articleCoverPath);
    }

    private void addArticleImage(Article article, String articleCoverPath) {
        articleRepository.addArticleImage(article, articleCoverPath);
    }

    private void validateImageSize(MultipartFile file) {
        long maxSizeBytes = maxImageSizeKb * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "O arquivo excede o tamanho máximo permitido de " + maxImageSizeKb + " KB."
            );
        }
    }
}

