package dev.falae.core.domain.entities;

import java.time.LocalDateTime;
import java.util.*;

public class Article {

    private UUID id;
    private Author author;
    private LocalDateTime creationDate;
    private Boolean isMarkdown;
    private String title;
    private String slug;
    private String coverImage;
    private String originalPost;
    private List<String> tags;
    private List<String> imagePaths;
    private String description;
    private Set<Author> authorsSavedArticle = new HashSet<>();
    private Set<Author> authorsLikedArticle = new HashSet<>();
    private int likesCount;
    private int savesCount;
    private int dislikesCount;
    private int commentsCount;
    private String urlArticleContent;
    private List<Comment> comments = new ArrayList<>();

    public Article(UUID id, Author author, LocalDateTime creationDate, Boolean isMarkdown, String title, String slug, String coverImage, String originalPost, List<String> tags, List<String> imagePaths, String description, String urlArticleContent, int likesCount, int savesCount, int dislikesCount, int commentsCount) {
        this.id = id;
        this.author = author;
        this.creationDate = creationDate;
        this.isMarkdown = isMarkdown;
        this.title = title;
        this.slug = slug;
        this.coverImage = coverImage;
        this.originalPost = originalPost;
        this.tags = tags;
        this.imagePaths = imagePaths;
        this.description = description;
        this.urlArticleContent = urlArticleContent;
        this.likesCount = likesCount;
        this.savesCount = savesCount;
        this.dislikesCount = dislikesCount;
        this.commentsCount = commentsCount;
    }
    public Article(String title, String slug, String originalPost, List<String> tags, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.originalPost = originalPost;
        this.tags = tags;
        this.description = description;
    }

    public Article(UUID id, Author author, Boolean isMarkdown) {
        this.id = id;
        this.author = author;
        this.isMarkdown = isMarkdown;
    }

// ---------- Getters e Setters ----------

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getMarkdown() {
        return isMarkdown;
    }

    public void setMarkdown(Boolean markdown) {
        isMarkdown = markdown;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getOriginalPost() {
        return originalPost;
    }

    public void setOriginalPost(String originalPost) {
        this.originalPost = originalPost;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Author> getAuthorsSavedArticle() {
        return authorsSavedArticle;
    }

    public void setAuthorsSavedArticle(Set<Author> authorsSavedArticle) {
        this.authorsSavedArticle = authorsSavedArticle;
    }

    public Set<Author> getAuthorsLikedArticle() {
        return authorsLikedArticle;
    }

    public void setAuthorsLikedArticle(Set<Author> authorsLikedArticle) {
        this.authorsLikedArticle = authorsLikedArticle;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getSavesCount() {
        return savesCount;
    }

    public void setSavesCount(int savesCount) {
        this.savesCount = savesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getUrlArticleContent() {
        return urlArticleContent;
    }

    public void setUrlArticleContent(String urlArticleContent) {
        this.urlArticleContent = urlArticleContent;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
