package dev.falae.core.domain.entities;

import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthorInteraction {

    private UUID id;
    private UUID recipientAuthorId;
    private UUID actorAuthorId;
    private String actorName;
    private String actorUserName;
    private String actorProfileImageUrl;
    private InteractionType interactionType;
    private TargetType targetType;
    private UUID targetId;
    private String targetTitle;
    private String targetSlug;
    private UUID commentId;
    private TargetType parentContentType;
    private boolean isRead;
    private LocalDateTime createdAt;

    public AuthorInteraction() {
    }

    public AuthorInteraction(UUID id, UUID recipientAuthorId, UUID actorAuthorId,
                              InteractionType interactionType, TargetType targetType,
                              UUID targetId, String targetTitle, String targetSlug,
                              boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.recipientAuthorId = recipientAuthorId;
        this.actorAuthorId = actorAuthorId;
        this.interactionType = interactionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.targetSlug = targetSlug;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static AuthorInteraction create(UUID recipientAuthorId, UUID actorAuthorId,
                                            InteractionType interactionType, TargetType targetType,
                                            UUID targetId, String targetTitle, String targetSlug) {
        return create(recipientAuthorId, actorAuthorId, interactionType, targetType, targetId, targetTitle, targetSlug, null, null);
    }

    public static AuthorInteraction create(UUID recipientAuthorId, UUID actorAuthorId,
                                            InteractionType interactionType, TargetType targetType,
                                            UUID targetId, String targetTitle, String targetSlug,
                                            UUID commentId) {
        return create(recipientAuthorId, actorAuthorId, interactionType, targetType, targetId, targetTitle, targetSlug, commentId, null);
    }

    public static AuthorInteraction create(UUID recipientAuthorId, UUID actorAuthorId,
                                            InteractionType interactionType, TargetType targetType,
                                            UUID targetId, String targetTitle, String targetSlug,
                                            UUID commentId, TargetType parentContentType) {
        AuthorInteraction interaction = new AuthorInteraction(
                UUID.randomUUID(),
                recipientAuthorId,
                actorAuthorId,
                interactionType,
                targetType,
                targetId,
                targetTitle,
                targetSlug,
                false,
                LocalDateTime.now()
        );
        interaction.setCommentId(commentId);
        interaction.setParentContentType(parentContentType);
        return interaction;
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

    public UUID getActorAuthorId() {
        return actorAuthorId;
    }

    public void setActorAuthorId(UUID actorAuthorId) {
        this.actorAuthorId = actorAuthorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorUserName() {
        return actorUserName;
    }

    public void setActorUserName(String actorUserName) {
        this.actorUserName = actorUserName;
    }

    public String getActorProfileImageUrl() {
        return actorProfileImageUrl;
    }

    public void setActorProfileImageUrl(String actorProfileImageUrl) {
        this.actorProfileImageUrl = actorProfileImageUrl;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
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

    public TargetType getParentContentType() {
        return parentContentType;
    }

    public void setParentContentType(TargetType parentContentType) {
        this.parentContentType = parentContentType;
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
