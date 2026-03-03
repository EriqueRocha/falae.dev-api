package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.TopicPageResponse;
import dev.falae.application.ports.dto.TopicResponse;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.core.domain.entities.Topic;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.entities.TopicEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.TopicJpaRepository;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaTopicRepository implements TopicRepository {

    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;
    private final TopicJpaRepository topicJpaRepository;
    private final JpaAuthorRepository jpaAuthorRepository;

    public JpaTopicRepository(AuthenticatedAuthorProvider authenticatedAuthorProvider, TopicJpaRepository topicJpaRepository, JpaAuthorRepository jpaAuthorRepository) {
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
        this.topicJpaRepository = topicJpaRepository;
        this.jpaAuthorRepository = jpaAuthorRepository;
    }

    @Override
    public Topic save(Topic topic) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        TopicEntity entity = toEntity(topic, author);
        topicJpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Topic findById(UUID topicId) {
        TopicEntity topicEntity = topicJpaRepository.findById(topicId)
                .orElseThrow(()-> new ResourceNotFoundException("Topic", topicId));

        return toDomain(topicEntity);
    }

    @Override
    public TopicResponse findTopicResponseById(UUID topicId) {
        TopicEntity topicEntity = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));

        return toTopicResponse(topicEntity);
    }

    @Override
    public Topic findByAuthorUserNameAndSlug(String userName, String slug) {
        TopicEntity topicEntity = topicJpaRepository.findByAuthorUserNameAndSlug(userName, slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found for author '" + userName + "' with slug '" + slug + "'"));

        return toDomain(topicEntity);
    }

    @Override
    public TopicResponse findTopicResponseByAuthorUserNameAndSlug(String userName, String slug) {
        TopicEntity topicEntity = topicJpaRepository.findByAuthorUserNameAndSlug(userName, slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found for author '" + userName + "' with slug '" + slug + "'"));

        return toTopicResponse(topicEntity);
    }

    @Override
    public TopicPageResponse findAll(int page, int size, FeedSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TopicEntity> topicPage = switch (sortType) {
            case RECENT -> topicJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
            case OLDEST -> topicJpaRepository.findAllByOrderByCreatedAtAsc(pageable);
            case LIKES -> topicJpaRepository.findAllByOrderByLikesCountDesc(pageable);
        };

        List<TopicResponse> topics = topicPage.getContent().stream()
                .map(this::toTopicResponse)
                .toList();

        return new TopicPageResponse(
                topics,
                topicPage.getNumber(),
                topicPage.getSize(),
                topicPage.getTotalElements(),
                topicPage.getTotalPages(),
                topicPage.hasNext()
        );
    }

    @Override
    public TopicPageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TopicEntity> topicPage = switch (sortType) {
            case RECENT -> topicJpaRepository.findByAuthorUserNameOrderByCreatedAtDesc(userName, pageable);
            case OLDEST -> topicJpaRepository.findByAuthorUserNameOrderByCreatedAtAsc(userName, pageable);
            case LIKES -> topicJpaRepository.findByAuthorUserNameOrderByLikesCountDesc(userName, pageable);
            case COMMENTS -> topicJpaRepository.findByAuthorUserNameOrderByCommentsCountDesc(userName, pageable);
            case SAVES -> throw new IllegalArgumentException("Topics do not support SAVES sorting");
        };

        List<TopicResponse> topics = topicPage.getContent().stream()
                .map(this::toTopicResponse)
                .toList();

        return new TopicPageResponse(
                topics,
                topicPage.getNumber(),
                topicPage.getSize(),
                topicPage.getTotalElements(),
                topicPage.getTotalPages(),
                topicPage.hasNext()
        );
    }

    @Override
    public TopicPageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TopicEntity> topicPage = switch (sortType) {
            case RECENT -> topicJpaRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title, pageable);
            case OLDEST -> topicJpaRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtAsc(title, pageable);
            case LIKES -> topicJpaRepository.findByTitleContainingIgnoreCaseOrderByLikesCountDesc(title, pageable);
            case COMMENTS -> topicJpaRepository.findByTitleContainingIgnoreCaseOrderByCommentsCountDesc(title, pageable);
            case SAVES -> throw new IllegalArgumentException("Topics do not support SAVES sorting");
        };

        List<TopicResponse> topics = topicPage.getContent().stream()
                .map(this::toTopicResponse)
                .toList();

        return new TopicPageResponse(
                topics,
                topicPage.getNumber(),
                topicPage.getSize(),
                topicPage.getTotalElements(),
                topicPage.getTotalPages(),
                topicPage.hasNext()
        );
    }

    @Override
    public long countByAuthorId(UUID authorId) {
        return topicJpaRepository.countByAuthorId(authorId);
    }

    private TopicResponse toTopicResponse(TopicEntity entity) {
        AuthorEntity currentAuthor = authenticatedAuthorProvider.getCurrentAuthorOrNull();

        Boolean isLiked = null;
        Boolean isDisliked = null;

        if (currentAuthor != null) {
            isLiked = entity.getAuthorsLikedTopic().contains(currentAuthor);
            isDisliked = entity.getAuthorsDislikedTopic().contains(currentAuthor);
        }

        return new TopicResponse(
                entity.getId(),
                entity.getAuthor() != null ? entity.getAuthor().getId() : null,
                entity.getAuthor() != null ? entity.getAuthor().getName() : null,
                entity.getAuthor() != null ? entity.getAuthor().getUserName() : null,
                entity.getAuthor() != null ? entity.getAuthor().getProfileImageUrl() : null,
                entity.getCreatedAt(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getTopicContent(),
                entity.getTags(),
                entity.getLikesCount(),
                entity.getDislikesCount(),
                entity.getCommentsCount(),
                isLiked,
                isDisliked
        );
    }

    @Override
    public void deleteById(UUID topicId){
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        TopicEntity topic = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));

        if (!topic.getAuthor().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Topic", topicId);
        }

        topicJpaRepository.delete(topic);
    }

    @Override
    public void adminDeleteById(UUID topicId) {
        TopicEntity topic = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));
        topicJpaRepository.delete(topic);
    }

    public TopicEntity toEntity(Topic topic, AuthorEntity author) {
        TopicEntity entity = new TopicEntity(
                author,
                topic.getTitle(),
                topic.getSlug(),
                topic.getTopicContent(),
                topic.getTags()
        );
        entity.setId(topic.getId());
        return entity;
    }

    public Topic toDomain(TopicEntity topic) {
        return new Topic(
                topic.getTopicContent(),
                topic.getId(),
                jpaAuthorRepository.toDomain(topic.getAuthor()),
                topic.getCreatedAt(),
                topic.getTitle(),
                topic.getSlug(),
                topic.getTags(),
                topic.getLikesCount(),
                topic.getDislikesCount(),
                topic.getCommentsCount()
        );
    }

    public TopicEntity getReferenceById(UUID topicId){
        return topicJpaRepository.getReferenceById(topicId);
    }

    @Override
    public long count() {
        return topicJpaRepository.count();
    }

    @Override
    public boolean toggleLike(UUID topicId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        TopicEntity topic = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));

        boolean wasLiked = topic.getAuthorsLikedTopic().contains(author);
        boolean wasDisliked = topic.getAuthorsDislikedTopic().contains(author);

        // Remove dislike if exists (mutual exclusivity)
        if (wasDisliked) {
            topic.getAuthorsDislikedTopic().remove(author);
            topic.setDislikesCount(topic.getDislikesCount() - 1);
        }

        if (wasLiked) {
            topic.getAuthorsLikedTopic().remove(author);
            topic.setLikesCount(topic.getLikesCount() - 1);
        } else {
            topic.getAuthorsLikedTopic().add(author);
            topic.setLikesCount(topic.getLikesCount() + 1);
        }

        topicJpaRepository.save(topic);
        return !wasLiked;
    }

    @Override
    public boolean toggleDislike(UUID topicId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        TopicEntity topic = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));

        boolean wasDisliked = topic.getAuthorsDislikedTopic().contains(author);
        boolean wasLiked = topic.getAuthorsLikedTopic().contains(author);

        // Remove like if exists (mutual exclusivity)
        if (wasLiked) {
            topic.getAuthorsLikedTopic().remove(author);
            topic.setLikesCount(topic.getLikesCount() - 1);
        }

        if (wasDisliked) {
            topic.getAuthorsDislikedTopic().remove(author);
            topic.setDislikesCount(topic.getDislikesCount() - 1);
        } else {
            topic.getAuthorsDislikedTopic().add(author);
            topic.setDislikesCount(topic.getDislikesCount() + 1);
        }

        topicJpaRepository.save(topic);
        return !wasDisliked;
    }

    @Override
    public boolean existsByCurrentAuthorAndTitle(String title) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        return topicJpaRepository.existsByAuthorIdAndTitle(author.getId(), title);
    }

    @Override
    public boolean existsByCurrentAuthorAndTitleExcludingId(String title, UUID topicId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        return topicJpaRepository.existsByAuthorIdAndTitleAndIdNot(author.getId(), title, topicId);
    }

    @Override
    public void update(UUID topicId, String title, String slug, String topicContent, List<String> tags) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        TopicEntity topic = topicJpaRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));

        if (!topic.getAuthor().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Topic", topicId);
        }

        if (title != null) {
            topic.setTitle(title);
            topic.setSlug(slug);
        }
        if (topicContent != null) {
            topic.setTopicContent(topicContent);
        }
        if (tags != null) {
            topic.setTags(tags);
        }

        topicJpaRepository.save(topic);
    }
}
