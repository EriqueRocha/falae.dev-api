package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.core.domain.entities.ForumConfig;
import dev.falae.infrastructure.adapters.repositories.entities.ForumConfigEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.ForumConfigJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaForumConfigRepository implements ForumConfigRepository {

    private final ForumConfigJpaRepository jpaRepository;

    public JpaForumConfigRepository(ForumConfigJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ForumConfig getConfig() {
        return jpaRepository.findAll()
                .stream()
                .findFirst()
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalStateException("ForumConfig not found in database"));
    }

    private ForumConfig toDomain(ForumConfigEntity entity) {
        ForumConfig config = new ForumConfig();
        config.setAmountCoinsStart(entity.getAmountCoinsStart());
        config.setCoinsFirstArticle(entity.getCoinsFirstArticle());
        config.setCoinsFirstComment(entity.getCoinsFirstComment());
        config.setCoinsFirstTopic(entity.getCoinsFirstTopic());
        config.setCoinsPerTopic(entity.getCoinsPerTopic());
        config.setCoinsPerComment(entity.getCoinsPerComment());
        config.setCoinsPerArticle(entity.getCoinsPerArticle());
        config.setStoreUnlocked(entity.isStoreUnlocked());
        config.setUserTitleUnlocked(entity.isUserTitleUnlocked());
        config.setArticleCreationUnlocked(entity.isArticleCreationUnlocked());
        config.setTopicCreationUnlocked(entity.isTopicCreationUnlocked());
        config.setCommentUnlocked(entity.isCommentUnlocked());
        config.setEmailVerificationRequired(entity.isEmailVerificationRequired());
        return config;
    }
}
