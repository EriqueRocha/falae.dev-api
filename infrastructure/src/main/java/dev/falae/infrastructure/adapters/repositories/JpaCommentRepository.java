package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.CommentPageResponse;
import dev.falae.application.ports.dto.CommentResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.core.domain.entities.Comment;
import dev.falae.infrastructure.adapters.repositories.entities.ArticleEntity;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.entities.CommentEntity;
import dev.falae.infrastructure.adapters.repositories.entities.TopicEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.CommentJpaRepository;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaCommentRepository implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;
    private final JpaAuthorRepository jpaAuthorRepository;
    private final JpaArticleRepository jpaArticleRepository;
    private final JpaTopicRepository jpaTopicRepository;

    public JpaCommentRepository(CommentJpaRepository commentJpaRepository, AuthenticatedAuthorProvider authenticatedAuthorProvider, JpaAuthorRepository jpaAuthorRepository, JpaArticleRepository jpaArticleRepository, JpaTopicRepository jpaTopicRepository) {
        this.commentJpaRepository = commentJpaRepository;
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
        this.jpaAuthorRepository = jpaAuthorRepository;
        this.jpaArticleRepository = jpaArticleRepository;
        this.jpaTopicRepository = jpaTopicRepository;
    }

    @Override
    public Comment save(Comment comment) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        CommentEntity entity = toEntity(comment, author);

        if (comment.getParentId() != null) {
            CommentEntity parent = commentJpaRepository.findById(comment.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Comment", comment.getParentId()));
            entity.setPath(parent.getPath() + "/" + comment.getId());
            entity.setDepth(parent.getDepth() + 1);
        } else {
            entity.setPath("/" + comment.getId());
            entity.setDepth(0);
        }

        commentJpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Comment findById(UUID commentId) {
        CommentEntity commentEntity = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        return toDomain(commentEntity);
    }

    @Override
    public CommentResponse findCommentResponseById(UUID commentId) {
        CommentEntity commentEntity = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        return toResponse(commentEntity);
    }

    private Comment toDomain(CommentEntity entity) {
        UUID parentId = entity.getParent() != null
                ? entity.getParent().getId()
                : null;

        if (entity.getArticle() != null) {

            return Comment.forArticle(
                    entity.getId(),
                    jpaAuthorRepository.toDomain(entity.getAuthor()),
                    jpaArticleRepository.toDomain(entity.getArticle()),
                    parentId,
                    entity.getLikes(),
                    entity.getCreatedAt(),
                    entity.getCommentContent(),
                    entity.getTags()
            );
        }

        if (entity.getTopic() != null) {
            return Comment.forTopic(
                    entity.getId(),
                    jpaAuthorRepository.toDomain(entity.getAuthor()),
                    jpaTopicRepository.toDomain(entity.getTopic()),
                    parentId,
                    entity.getLikes(),
                    entity.getCreatedAt(),
                    entity.getCommentContent(),
                    entity.getTags()
            );
        }

        throw new IllegalStateException("Inválid comment: without article and topic");
    }

    private CommentEntity toEntity(Comment comment, AuthorEntity author) {
        CommentEntity entity = new CommentEntity();

        entity.setId(comment.getId());
        entity.setAuthor(author);
        entity.setCreatedAt(comment.getCreationDate());
        entity.setCommentContent(comment.getCommentContent());
        entity.setTags(comment.getTags());

        if (comment.getParentId() != null) {
            CommentEntity parentRef = commentJpaRepository.getReferenceById(comment.getParentId());
            entity.setParent(parentRef);
        } else {
            entity.setParent(null);
        }
        if (comment.getArticle() != null) {
            ArticleEntity articleRef = jpaArticleRepository.getReferenceById(comment.getArticle().getId());
            entity.setArticle(articleRef);
            entity.setTopic(null);
        } else if (comment.getTopic() != null) {
            TopicEntity topicRef = jpaTopicRepository.getReferenceById(comment.getTopic().getId());
            entity.setTopic(topicRef);
            entity.setArticle(null);
        } else {
            throw new IllegalStateException("Comment must have either article or topic.");
        }

        return entity;
    }

    @Override
    public CommentPageResponse findAll(int page, int size, FeedSortType sortType) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<CommentEntity> pageResult = switch (sortType) {
            case RECENT -> commentJpaRepository.findAllCommentsOrderByCreatedAtDesc(pageable);
            case OLDEST -> commentJpaRepository.findAllCommentsOrderByCreatedAtAsc(pageable);
            case LIKES -> commentJpaRepository.findAllCommentsOrderByLikesDesc(pageable);
        };

        List<CommentResponse> comments = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new CommentPageResponse(
                comments,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );
    }

    @Override
    public CommentPageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<CommentEntity> pageResult = switch (sortType) {
            case RECENT -> commentJpaRepository.findByAuthorUserNameOrderByCreatedAtDesc(userName, pageable);
            case OLDEST -> commentJpaRepository.findByAuthorUserNameOrderByCreatedAtAsc(userName, pageable);
            case LIKES -> commentJpaRepository.findByAuthorUserNameOrderByLikesDesc(userName, pageable);
            case SAVES, COMMENTS -> commentJpaRepository.findByAuthorUserNameOrderByCreatedAtDesc(userName, pageable);
        };

        List<CommentResponse> comments = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new CommentPageResponse(
                comments,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );
    }

    @Override
    public long countByAuthorId(UUID authorId) {
        return commentJpaRepository.countByAuthorId(authorId);
    }

    @Override
    public CommentPageResponse findRootCommentsByArticleId(UUID articleId, int page, int size) {
        Page<CommentEntity> pageResult = commentJpaRepository
                .findRootCommentsByArticleId(articleId, PageRequest.of(page, size));

        List<CommentResponse> comments = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new CommentPageResponse(
                comments,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );
    }

    @Override
    public CommentPageResponse findRootCommentsByTopicId(UUID topicId, int page, int size) {
        Page<CommentEntity> pageResult = commentJpaRepository
                .findRootCommentsByTopicId(topicId, PageRequest.of(page, size));

        List<CommentResponse> comments = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new CommentPageResponse(
                comments,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );
    }

    @Override
    public List<CommentResponse> findReplies(UUID parentId) {
        return commentJpaRepository.findDirectReplies(parentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public long countReplies(UUID parentId) {
        return commentJpaRepository.countDirectReplies(parentId);
    }

    @Override
    public void deleteById(UUID commentId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        CommentEntity comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Comment", commentId);
        }

        boolean hasReplies = commentJpaRepository.countDirectReplies(commentId) > 0;

        if (hasReplies) {
            comment.setDeleted(true);
            comment.setCommentContent(null);
            commentJpaRepository.save(comment);
        } else {
            commentJpaRepository.delete(comment);
        }
    }

    @Override
    public void adminDeleteById(UUID commentId) {
        CommentEntity comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        boolean hasReplies = commentJpaRepository.countDirectReplies(commentId) > 0;

        if (hasReplies) {
            comment.setDeleted(true);
            comment.setCommentContent(null);
            commentJpaRepository.save(comment);
        } else {
            commentJpaRepository.delete(comment);
        }
    }

    private CommentResponse toResponse(CommentEntity entity) {
        UUID parentId = entity.getParent() != null ? entity.getParent().getId() : null;
        UUID articleId = entity.getArticle() != null ? entity.getArticle().getId() : null;
        UUID topicId = entity.getTopic() != null ? entity.getTopic().getId() : null;

        String parentAuthorUserName = null;
        String parentTitle = null;
        String parentSlug = null;
        if (entity.getArticle() != null) {
            parentAuthorUserName = entity.getArticle().getAuthor() != null
                    ? entity.getArticle().getAuthor().getUserName() : null;
            parentTitle = entity.getArticle().getTitle();
            parentSlug = entity.getArticle().getSlug();
        } else if (entity.getTopic() != null) {
            parentAuthorUserName = entity.getTopic().getAuthor() != null
                    ? entity.getTopic().getAuthor().getUserName() : null;
            parentTitle = entity.getTopic().getTitle();
            parentSlug = entity.getTopic().getSlug();
        }

        AuthorEntity currentAuthor = authenticatedAuthorProvider.getCurrentAuthorOrNull();

        Boolean isLiked = null;
        Boolean isDisliked = null;
        Boolean isOwner = null;

        if (currentAuthor != null && !entity.isDeleted()) {
            isLiked = entity.getLikedByAuthors().contains(currentAuthor);
            isDisliked = entity.getDislikedByAuthors().contains(currentAuthor);
            isOwner = entity.getAuthor().getId().equals(currentAuthor.getId());
        }

        return new CommentResponse(
                entity.getId(),
                entity.isDeleted() ? null : entity.getCommentContent(),
                entity.isDeleted() ? null : entity.getAuthor().getName(),
                entity.isDeleted() ? null : entity.getAuthor().getUserName(),
                entity.isDeleted() ? null : entity.getAuthor().getProfileImageUrl(),
                entity.isDeleted() ? null : entity.getAuthor().getId(),
                entity.getCreatedAt(),
                entity.isDeleted() ? 0 : entity.getLikes(),
                entity.isDeleted() ? 0 : entity.getDislikes(),
                entity.getDepth(),
                commentJpaRepository.countDirectReplies(entity.getId()),
                parentId,
                articleId,
                topicId,
                entity.isDeleted(),
                parentAuthorUserName,
                parentTitle,
                parentSlug,
                isLiked,
                isDisliked,
                isOwner,
                entity.isDeleted() ? null : entity.getTags()
        );
    }

    @Override
    public long count() {
        return commentJpaRepository.count();
    }

    @Override
    public boolean toggleLike(UUID commentId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        CommentEntity comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        boolean wasLiked = comment.getLikedByAuthors().contains(author);
        boolean wasDisliked = comment.getDislikedByAuthors().contains(author);

        if (wasDisliked) {
            comment.getDislikedByAuthors().remove(author);
            comment.setDislikes(comment.getDislikes() - 1);
        }

        if (wasLiked) {
            comment.getLikedByAuthors().remove(author);
            comment.setLikes(comment.getLikes() - 1);
        } else {
            comment.getLikedByAuthors().add(author);
            comment.setLikes(comment.getLikes() + 1);
        }

        commentJpaRepository.save(comment);
        return !wasLiked;
    }

    @Override
    public boolean toggleDislike(UUID commentId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        CommentEntity comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        boolean wasDisliked = comment.getDislikedByAuthors().contains(author);
        boolean wasLiked = comment.getLikedByAuthors().contains(author);

        if (wasLiked) {
            comment.getLikedByAuthors().remove(author);
            comment.setLikes(comment.getLikes() - 1);
        }

        if (wasDisliked) {
            comment.getDislikedByAuthors().remove(author);
            comment.setDislikes(comment.getDislikes() - 1);
        } else {
            comment.getDislikedByAuthors().add(author);
            comment.setDislikes(comment.getDislikes() + 1);
        }

        commentJpaRepository.save(comment);
        return !wasDisliked;
    }

}
