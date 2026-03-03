package dev.falae.infrastructure.adapters.repositories.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "topics", schema = "public")
public class TopicEntity extends BaseEntity {

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @Column(length = 255)
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String title;

    @Column(length = 255)
    private String slug;

    @Column(length = 10000)
    @Size(max = 10000, message = "O conteúdo do tópico deve ter no máximo 10000 caracteres.")
    private String topicContent;

    @Column(columnDefinition = "TEXT[]")
    private List<String> tags;

    @ManyToMany
    @JoinTable(
            name = "authors_liked_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> authorsLikedTopic = new HashSet<>();

    @Column(name = "likes_count", nullable = false)
    private int likesCount = 0;

    @Column(name = "dislikes_count", nullable = false)
    private int dislikesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount = 0;

    @ManyToMany
    @JoinTable(
            name = "authors_disliked_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> authorsDislikedTopic = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    protected TopicEntity() {
    }

    public TopicEntity(AuthorEntity author, String title, String slug, String topicContent, List<String> tags) {
        this.author = author;
        this.title = title;
        this.slug = slug;
        this.topicContent = topicContent;
        this.tags = tags;
    }

    // ---------- Getters e Setters ----------


    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
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

    public Set<AuthorEntity> getAuthorsLikedTopic() {
        return authorsLikedTopic;
    }

    public void setAuthorsLikedTopic(Set<AuthorEntity> authorsLikedTopic) {
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

    public Set<AuthorEntity> getAuthorsDislikedTopic() {
        return authorsDislikedTopic;
    }

    public void setAuthorsDislikedTopic(Set<AuthorEntity> authorsDislikedTopic) {
        this.authorsDislikedTopic = authorsDislikedTopic;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
