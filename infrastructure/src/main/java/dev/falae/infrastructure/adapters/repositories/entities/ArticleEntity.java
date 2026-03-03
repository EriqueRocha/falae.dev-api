package dev.falae.infrastructure.adapters.repositories.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

import java.util.*;

@Entity
@Table(name = "articles")
public class ArticleEntity extends BaseEntity {

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    private Boolean isMarkdown;

    @Column(length = 255)
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String title;

    @Column(length = 255)
    private String slug;

    private String coverImage;

    @URL(message = "O originalPost deve ser uma URL válida.")
    private String originalPost;

    private List<String> tags;

    @ElementCollection
    @CollectionTable(
            name = "article_entity_image_paths",
            joinColumns = @JoinColumn(name = "article_entity_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> imagePaths = new ArrayList<>();

    @Column(length = 500)
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "clients_saved_article",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<AuthorEntity> authorsSavedArticle = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "clients_liked_article",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<AuthorEntity> authorsLikedArticle = new HashSet<>();

    @Column(name = "likes_count", nullable = false)
    private int likesCount = 0;

    @Column(name = "saves_count", nullable = false)
    private int savesCount = 0;

    @Column(name = "dislikes_count", nullable = false)
    private int dislikesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount = 0;

    @ManyToMany
    @JoinTable(
            name = "clients_disliked_article",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<AuthorEntity> authorsDislikedArticle = new HashSet<>();

    private String urlArticleContent;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CommentEntity> comments = new ArrayList<>();

    public ArticleEntity(AuthorEntity author, Boolean isMarkdown, String title, String slug, String coverImage, String originalPost, List<String> tags, List<String> imagePaths, String description, String urlArticleContent) {
        this.author = author;
        this.isMarkdown = isMarkdown;
        this.title = title;
        this.slug = slug;
        this.coverImage = coverImage;
        this.originalPost = originalPost;
        this.tags = tags;
        this.imagePaths = imagePaths;
        this.description = description;
        this.urlArticleContent = urlArticleContent;
    }

    public ArticleEntity() {
    }

    // ---------- Getters e Setters ----------


    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public Boolean isMarkdown() {
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

    public Set<AuthorEntity> getAuthorsSavedArticle() {
        return authorsSavedArticle;
    }

    public void setAuthorsSavedArticle(Set<AuthorEntity> authorsSavedArticle) {
        this.authorsSavedArticle = authorsSavedArticle;
    }

    public Set<AuthorEntity> getAuthorsLikedArticle() {
        return authorsLikedArticle;
    }

    public void setAuthorsLikedArticle(Set<AuthorEntity> authorsLikedArticle) {
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

    public Set<AuthorEntity> getAuthorsDislikedArticle() {
        return authorsDislikedArticle;
    }

    public void setAuthorsDislikedArticle(Set<AuthorEntity> authorsDislikedArticle) {
        this.authorsDislikedArticle = authorsDislikedArticle;
    }

    public String getUrlArticleContent() {
        return urlArticleContent;
    }

    public void setUrlArticleContent(String urlArticleContent) {
        this.urlArticleContent = urlArticleContent;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }
}
