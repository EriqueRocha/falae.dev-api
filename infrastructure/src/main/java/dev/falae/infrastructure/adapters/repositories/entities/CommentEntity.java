package dev.falae.infrastructure.adapters.repositories.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name = "comments", schema = "public")
public class CommentEntity extends BaseEntity{

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "topic_id")
    private TopicEntity topic;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;//se for um sub comentário aqui será o comentário pai

    @JsonManagedReference
    @OneToMany(mappedBy = "parent")
    private List<CommentEntity> subComments = new ArrayList<>();

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "depth")
    private int depth;

    @Column(length = 3600)
    @Size(max = 3600, message = "O comentário deve ter no máximo 3600 caracteres.")
    private String commentContent;

    @Column(columnDefinition = "TEXT[]")
    private List<String> tags;

    private int likes;

    private int dislikes;

    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> likedByAuthors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "comment_dislikes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> dislikedByAuthors = new HashSet<>();

    public CommentEntity() {
    }

    public CommentEntity(AuthorEntity author, ArticleEntity article, TopicEntity topic, CommentEntity parent, List<CommentEntity> subComments, String commentContent, int likes, Set<AuthorEntity> likedByAuthors) {
        this.author = author;
        this.article = article;
        this.topic = topic;
        this.parent = parent;
        this.subComments = subComments;
        this.commentContent = commentContent;
    }

    // ---------- Getters e Setters ----------


    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }

    public TopicEntity getTopic() {
        return topic;
    }

    public void setTopic(TopicEntity topic) {
        this.topic = topic;
    }

    public CommentEntity getParent() {
        return parent;
    }

    public void setParent(CommentEntity parent) {
        this.parent = parent;
    }

    public List<CommentEntity> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<CommentEntity> subComments) {
        this.subComments = subComments;
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

    public Set<AuthorEntity> getLikedByAuthors() {
        return likedByAuthors;
    }

    public void setLikedByAuthors(Set<AuthorEntity> likedByAuthors) {
        this.likedByAuthors = likedByAuthors;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Set<AuthorEntity> getDislikedByAuthors() {
        return dislikedByAuthors;
    }

    public void setDislikedByAuthors(Set<AuthorEntity> dislikedByAuthors) {
        this.dislikedByAuthors = dislikedByAuthors;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
