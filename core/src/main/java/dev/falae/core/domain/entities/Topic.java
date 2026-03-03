package dev.falae.core.domain.entities;

import java.time.LocalDateTime;
import java.util.*;

public class Topic {

    private UUID id;
    private Author author;
    private LocalDateTime creationDate;
    private String title;
    private String slug;
    private String topicContent;
    private List<String> tags = new ArrayList<>();
    private Set<Author> authorsLikedTopic = new HashSet<>();
    private int likesCount;
    private int dislikesCount;
    private int commentsCount;
    private List<Comment> comments = new ArrayList<>();

    public Topic(String topicContent, UUID id, Author author, LocalDateTime creationDate, String title, String slug, List<String> tags, int likesCount, int dislikesCount, int commentsCount) {
        this.topicContent = topicContent;
        this.id = id;
        this.author = author;
        this.creationDate = creationDate;
        this.title = title;
        this.slug = slug;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.commentsCount = commentsCount;
    }

    public Topic(String topicContent, String title, String slug, List<String> tags) {
        this.id = UUID.randomUUID();
        this.topicContent = topicContent;
        this.title = title;
        this.slug = slug;
        this.tags = tags != null ? tags : new ArrayList<>();
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

    public String getTopicContent() {
        return topicContent;
    }

    public void setTopicContent(String topicContent) {
        this.topicContent = topicContent;
    }

    public Set<Author> getAuthorsLikedTopic() {
        return authorsLikedTopic;
    }

    public void setAuthorsLikedTopic(Set<Author> authorsLikedTopic) {
        this.authorsLikedTopic = authorsLikedTopic;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
