package dev.falae.core.domain.entities;

import java.time.LocalDateTime;
import java.util.*;

public class Comment {

    private UUID id;
    private Author author;
    private Article article;
    private Topic topic;
    private UUID parentId;//se for um sub comentário aqui será o comentário pai
    private List<UUID> subCommentIds = new ArrayList<>();
    private LocalDateTime creationDate;
    private String commentContent;
    private List<String> tags = new ArrayList<>();
    private int likes;
    private int dislikes;
    private Set<Author> likedByAuthors = new HashSet<>();

    private Comment(String commentContent, Article article, Topic topic, UUID parentId, List<String> tags) {
        if (article == null && topic == null) {
            throw new IllegalArgumentException("Comment must have either an article or a topic");
        }

        this.id = UUID.randomUUID();
        this.commentContent = commentContent;
        this.article = article;
        this.topic = topic;
        this.parentId = parentId;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public static Comment forArticle(String content, Article article, UUID parentId, List<String> tags) {
        return new Comment(content, article, null, parentId, tags);
    }

    public static Comment forTopic(String content, Topic topic, UUID parentId, List<String> tags) {
        return new Comment(content, null, topic, parentId, tags);
    }

    private Comment(
            UUID id,
            Author author,
            Article article,
            Topic topic,
            int likes,
            UUID parentId,
            LocalDateTime creationDate,
            String commentContent,
            List<String> tags
    ) {
        this.id = id;
        this.author = author;
        this.article = article;
        this.topic = topic;
        this.likes = likes;
        this.parentId = parentId;
        this.creationDate = creationDate;
        this.commentContent = commentContent;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public static Comment forArticle(
            UUID id,
            Author author,
            Article article,
            UUID parentId,
            int likes,
            LocalDateTime creationDate,
            String commentContent,
            List<String> tags
    ) {
        return new Comment(id, author, article, null, likes, parentId, creationDate, commentContent, tags);
    }

    public static Comment forTopic(
            UUID id,
            Author author,
            Topic topic,
            UUID parentId,
            int likes,
            LocalDateTime creationDate,
            String commentContent,
            List<String> tags
    ) {
        return new Comment(id, author, null, topic, likes, parentId, creationDate, commentContent, tags);
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

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public List<UUID> getSubCommentIds() {
        return subCommentIds;
    }

    public void setSubCommentIds(List<UUID> subCommentIds) {
        this.subCommentIds = subCommentIds;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Set<Author> getLikedByAuthors() {
        return likedByAuthors;
    }

    public void setLikedByAuthors(Set<Author> likedByAuthors) {
        this.likedByAuthors = likedByAuthors;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
