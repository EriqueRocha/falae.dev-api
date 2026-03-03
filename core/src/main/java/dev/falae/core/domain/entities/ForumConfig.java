package dev.falae.core.domain.entities;

public class ForumConfig {

    private int amountCoinsStart;
    private int coinsFirstArticle;
    private int coinsFirstComment;
    private int coinsFirstTopic;
    private int coinsPerTopic;
    private int coinsPerComment;
    private int coinsPerArticle;
    private boolean storeUnlocked;
    private boolean userTitleUnlocked;
    private boolean articleCreationUnlocked;
    private boolean topicCreationUnlocked;
    private boolean commentUnlocked;
    private boolean emailVerificationRequired;

// ---------- Getters e Setters ----------


    public int getAmountCoinsStart() {
        return amountCoinsStart;
    }

    public void setAmountCoinsStart(int amountCoinsStart) {
        this.amountCoinsStart = amountCoinsStart;
    }

    public int getCoinsFirstArticle() {
        return coinsFirstArticle;
    }

    public void setCoinsFirstArticle(int coinsFirstArticle) {
        this.coinsFirstArticle = coinsFirstArticle;
    }

    public int getCoinsFirstComment() {
        return coinsFirstComment;
    }

    public void setCoinsFirstComment(int coinsFirstComment) {
        this.coinsFirstComment = coinsFirstComment;
    }

    public int getCoinsFirstTopic() {
        return coinsFirstTopic;
    }

    public void setCoinsFirstTopic(int coinsFirstTopic) {
        this.coinsFirstTopic = coinsFirstTopic;
    }

    public int getCoinsPerTopic() {
        return coinsPerTopic;
    }

    public void setCoinsPerTopic(int coinsPerTopic) {
        this.coinsPerTopic = coinsPerTopic;
    }

    public int getCoinsPerComment() {
        return coinsPerComment;
    }

    public void setCoinsPerComment(int coinsPerComment) {
        this.coinsPerComment = coinsPerComment;
    }

    public int getCoinsPerArticle() {
        return coinsPerArticle;
    }

    public void setCoinsPerArticle(int coinsPerArticle) {
        this.coinsPerArticle = coinsPerArticle;
    }

    public boolean isStoreUnlocked() {
        return storeUnlocked;
    }

    public void setStoreUnlocked(boolean storeUnlocked) {
        this.storeUnlocked = storeUnlocked;
    }

    public boolean isUserTitleUnlocked() {
        return userTitleUnlocked;
    }

    public void setUserTitleUnlocked(boolean userTitleUnlocked) {
        this.userTitleUnlocked = userTitleUnlocked;
    }

    public boolean isArticleCreationUnlocked() {
        return articleCreationUnlocked;
    }

    public void setArticleCreationUnlocked(boolean articleCreationUnlocked) {
        this.articleCreationUnlocked = articleCreationUnlocked;
    }

    public boolean isTopicCreationUnlocked() {
        return topicCreationUnlocked;
    }

    public void setTopicCreationUnlocked(boolean topicCreationUnlocked) {
        this.topicCreationUnlocked = topicCreationUnlocked;
    }

    public boolean isCommentUnlocked() {
        return commentUnlocked;
    }

    public void setCommentUnlocked(boolean commentUnlocked) {
        this.commentUnlocked = commentUnlocked;
    }

    public boolean isEmailVerificationRequired() {
        return emailVerificationRequired;
    }

    public void setEmailVerificationRequired(boolean emailVerificationRequired) {
        this.emailVerificationRequired = emailVerificationRequired;
    }
}
