package dev.falae.infrastructure.adapters.repositories.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "author_interactions", schema = "public")
public class AuthorInteractionEntity {

    @Id
    private UUID id;

    @Column(name = "recipient_author_id", nullable = false)
    private UUID recipientAuthorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_author_id", nullable = false, insertable = false, updatable = false)
    private AuthorEntity actor;

    @Column(name = "actor_author_id", nullable = false)
    private UUID actorAuthorId;

    @Column(name = "interaction_type", nullable = false, length = 30)
    private String interactionType;

    @Column(name = "target_type", nullable = false, length = 15)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "target_title", length = 255)
    private String targetTitle;

    @Column(name = "target_slug", length = 255)
    private String targetSlug;

    @Column(name = "comment_id")
    private UUID commentId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuthorInteractionEntity() {
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRecipientAuthorId() {
        return recipientAuthorId;
    }

    public void setRecipientAuthorId(UUID recipientAuthorId) {
        this.recipientAuthorId = recipientAuthorId;
    }

    public AuthorEntity getActor() {
        return actor;
    }

    public void setActor(AuthorEntity actor) {
        this.actor = actor;
    }

    public UUID getActorAuthorId() {
        return actorAuthorId;
    }

    public void setActorAuthorId(UUID actorAuthorId) {
        this.actorAuthorId = actorAuthorId;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getTargetSlug() {
        return targetSlug;
    }

    public void setTargetSlug(String targetSlug) {
        this.targetSlug = targetSlug;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
