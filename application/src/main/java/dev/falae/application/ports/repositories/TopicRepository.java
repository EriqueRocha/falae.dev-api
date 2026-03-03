package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.TopicPageResponse;
import dev.falae.application.ports.dto.TopicResponse;
import dev.falae.core.domain.entities.Topic;

import java.util.UUID;

public interface TopicRepository {
    Topic save(Topic topic);
    Topic findById(UUID topicId);
    TopicResponse findTopicResponseById(UUID topicId);
    Topic findByAuthorUserNameAndSlug(String userName, String slug);
    TopicResponse findTopicResponseByAuthorUserNameAndSlug(String userName, String slug);
    TopicPageResponse findAll(int page, int size, FeedSortType sortType);
    TopicPageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType);
    TopicPageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType);
    long countByAuthorId(UUID authorId);
    void deleteById(UUID topicId);
    void adminDeleteById(UUID topicId);
    long count();
    boolean toggleLike(UUID topicId);
    boolean toggleDislike(UUID topicId);
    boolean existsByCurrentAuthorAndTitle(String title);
    boolean existsByCurrentAuthorAndTitleExcludingId(String title, UUID topicId);
    void update(UUID topicId, String title, String slug, String topicContent, java.util.List<String> tags);
}
