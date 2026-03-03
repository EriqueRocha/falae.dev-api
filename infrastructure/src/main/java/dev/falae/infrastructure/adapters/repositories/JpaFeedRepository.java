package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.ports.dto.FeedItemResponse;
import dev.falae.application.ports.dto.FeedItemResponse.FeedItemType;
import dev.falae.application.ports.dto.FeedPageResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.FeedRepository;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JpaFeedRepository implements FeedRepository {

    private final EntityManager entityManager;
    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;

    public JpaFeedRepository(EntityManager entityManager, AuthenticatedAuthorProvider authenticatedAuthorProvider) {
        this.entityManager = entityManager;
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
    }

    @Override
    public FeedPageResponse findAll(int page, int size, FeedSortType sortType, FeedItemType filterType) {
        String orderBy = switch (sortType) {
            case RECENT -> "created_at DESC";
            case OLDEST -> "created_at ASC";
            case LIKES -> "likes_count DESC, created_at DESC";
        };

        String typeFilter = filterType != null ? " WHERE type = :filterType" : "";

        AuthorEntity currentAuthor = authenticatedAuthorProvider.getCurrentAuthorOrNull();
        UUID currentAuthorId = currentAuthor != null ? currentAuthor.getId() : null;

        String sql = buildUnionQuery(typeFilter, orderBy, currentAuthorId != null);
        String countSql = buildCountQuery(typeFilter);

        Query query = entityManager.createNativeQuery(sql);
        Query countQuery = entityManager.createNativeQuery(countSql);

        if (filterType != null) {
            query.setParameter("filterType", filterType.name());
            countQuery.setParameter("filterType", filterType.name());
        }

        if (currentAuthorId != null) {
            query.setParameter("currentAuthorId", currentAuthorId);
        }

        query.setParameter("offset", page * size);
        query.setParameter("limit", size);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        long totalElements = ((Number) countQuery.getSingleResult()).longValue();

        List<FeedItemResponse> items = results.stream()
                .map(row -> mapToFeedItem(row, currentAuthorId != null))
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;

        return new FeedPageResponse(
                items,
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );
    }

    private String buildUnionQuery(String typeFilter, String orderBy, boolean hasCurrentUser) {
        String userInteractionColumns = hasCurrentUser ? """
                    ,
                    CASE WHEN al.client_id IS NOT NULL THEN true ELSE false END as is_liked,
                    CASE WHEN ad.client_id IS NOT NULL THEN true ELSE false END as is_disliked,
                    CASE WHEN asv.client_id IS NOT NULL THEN true ELSE false END as is_saved,
                    CASE WHEN a.author_id = :currentAuthorId THEN true ELSE false END as is_owner
                """ : """
                    ,
                    NULL as is_liked,
                    NULL as is_disliked,
                    NULL as is_saved,
                    NULL as is_owner
                """;

        String articleUserJoins = hasCurrentUser ? """
                LEFT JOIN clients_liked_article al ON a.id = al.article_id AND al.client_id = :currentAuthorId
                LEFT JOIN clients_disliked_article ad ON a.id = ad.article_id AND ad.client_id = :currentAuthorId
                LEFT JOIN clients_saved_article asv ON a.id = asv.article_id AND asv.client_id = :currentAuthorId
                """ : "";

        String topicUserInteractionColumns = hasCurrentUser ? """
                    ,
                    CASE WHEN tl.author_id IS NOT NULL THEN true ELSE false END as is_liked,
                    CASE WHEN td.author_id IS NOT NULL THEN true ELSE false END as is_disliked,
                    false as is_saved,
                    CASE WHEN t.author_id = :currentAuthorId THEN true ELSE false END as is_owner
                """ : """
                    ,
                    NULL as is_liked,
                    NULL as is_disliked,
                    NULL as is_saved,
                    NULL as is_owner
                """;

        String topicUserJoins = hasCurrentUser ? """
                LEFT JOIN authors_liked_topic tl ON t.id = tl.topic_id AND tl.author_id = :currentAuthorId
                LEFT JOIN authors_disliked_topic td ON t.id = td.topic_id AND td.author_id = :currentAuthorId
                """ : "";

        String commentUserInteractionColumns = hasCurrentUser ? """
                    ,
                    CASE WHEN cl.author_id IS NOT NULL THEN true ELSE false END as is_liked,
                    CASE WHEN cd.author_id IS NOT NULL THEN true ELSE false END as is_disliked,
                    false as is_saved,
                    CASE WHEN c.author_id = :currentAuthorId THEN true ELSE false END as is_owner
                """ : """
                    ,
                    NULL as is_liked,
                    NULL as is_disliked,
                    NULL as is_saved,
                    NULL as is_owner
                """;

        String commentUserJoins = hasCurrentUser ? """
                LEFT JOIN comment_likes cl ON c.id = cl.comment_id AND cl.author_id = :currentAuthorId
                LEFT JOIN comment_dislikes cd ON c.id = cd.comment_id AND cd.author_id = :currentAuthorId
                """ : "";

        return """
            SELECT * FROM (
                SELECT
                    a.id,
                    'ARTICLE' as type,
                    a.title,
                    a.slug,
                    a.description as content,
                    a.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    au.profile_image_url as author_profile_image,
                    a.created_at,
                    a.likes_count,
                    a.dislikes_count,
                    a.comments_count,
                    a.saves_count,
                    a.cover_image,
                    a.tags,
                    NULL as parent_type,
                    NULL as parent_author_user_name,
                    NULL as parent_title,
                    NULL as parent_slug
                    """ + userInteractionColumns + """
                FROM articles a
                LEFT JOIN authors au ON a.author_id = au.id
                """ + articleUserJoins + """

                UNION ALL

                SELECT
                    t.id,
                    'TOPIC' as type,
                    t.title,
                    t.slug,
                    t.topic_content as content,
                    t.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    au.profile_image_url as author_profile_image,
                    t.created_at,
                    t.likes_count,
                    t.dislikes_count,
                    t.comments_count,
                    0 as saves_count,
                    NULL as cover_image,
                    t.tags,
                    NULL as parent_type,
                    NULL as parent_author_user_name,
                    NULL as parent_title,
                    NULL as parent_slug
                    """ + topicUserInteractionColumns + """
                FROM topics t
                LEFT JOIN authors au ON t.author_id = au.id
                """ + topicUserJoins + """

                UNION ALL

                SELECT
                    c.id,
                    'COMMENT' as type,
                    NULL as title,
                    NULL as slug,
                    c.comment_content as content,
                    c.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    au.profile_image_url as author_profile_image,
                    c.created_at,
                    c.likes as likes_count,
                    c.dislikes as dislikes_count,
                    0 as comments_count,
                    0 as saves_count,
                    NULL as cover_image,
                    c.tags,
                    CASE WHEN c.article_id IS NOT NULL THEN 'ARTICLE' ELSE 'TOPIC' END as parent_type,
                    COALESCE(art_author.user_name, top_author.user_name) as parent_author_user_name,
                    COALESCE(art.title, top.title) as parent_title,
                    COALESCE(art.slug, top.slug) as parent_slug
                    """ + commentUserInteractionColumns + """
                FROM comments c
                LEFT JOIN authors au ON c.author_id = au.id
                LEFT JOIN articles art ON c.article_id = art.id
                LEFT JOIN authors art_author ON art.author_id = art_author.id
                LEFT JOIN topics top ON c.topic_id = top.id
                LEFT JOIN authors top_author ON top.author_id = top_author.id
                """ + commentUserJoins + """
                WHERE c.deleted = false
            ) AS feed
            """ + typeFilter + " ORDER BY " + orderBy + " OFFSET :offset LIMIT :limit";
    }

    private String buildCountQuery(String typeFilter) {
        return """
            SELECT COUNT(*) FROM (
                SELECT a.id, 'ARTICLE' as type FROM articles a
                UNION ALL
                SELECT t.id, 'TOPIC' as type FROM topics t
                UNION ALL
                SELECT c.id, 'COMMENT' as type FROM comments c WHERE c.deleted = false
            ) AS feed
            """ + typeFilter;
    }

    private FeedItemResponse mapToFeedItem(Object[] row, boolean hasCurrentUser) {
        UUID id = row[0] instanceof UUID ? (UUID) row[0] : UUID.fromString(row[0].toString());
        FeedItemType type = FeedItemType.valueOf((String) row[1]);
        String title = (String) row[2];
        String slug = (String) row[3];
        String content = (String) row[4];
        UUID authorId = row[5] != null ? (row[5] instanceof UUID ? (UUID) row[5] : UUID.fromString(row[5].toString())) : null;
        String authorName = (String) row[6];
        String authorUserName = (String) row[7];
        String authorProfileImage = (String) row[8];
        LocalDateTime createdAt = row[9] instanceof Timestamp ? ((Timestamp) row[9]).toLocalDateTime() : (LocalDateTime) row[9];
        int likesCount = row[10] != null ? ((Number) row[10]).intValue() : 0;
        int dislikesCount = row[11] != null ? ((Number) row[11]).intValue() : 0;
        int commentsCount = row[12] != null ? ((Number) row[12]).intValue() : 0;
        int savesCount = row[13] != null ? ((Number) row[13]).intValue() : 0;
        String coverImage = (String) row[14];

        List<String> tags = null;
        if (row[15] != null) {
            if (row[15] instanceof String[]) {
                tags = Arrays.asList((String[]) row[15]);
            } else if (row[15] instanceof String) {
                String tagsStr = (String) row[15];
                if (tagsStr.startsWith("{") && tagsStr.endsWith("}")) {
                    tagsStr = tagsStr.substring(1, tagsStr.length() - 1);
                    tags = tagsStr.isEmpty() ? List.of() : Arrays.asList(tagsStr.split(","));
                }
            }
        }

        FeedItemType parentType = row[16] != null ? FeedItemType.valueOf((String) row[16]) : null;
        String parentAuthorUserName = (String) row[17];
        String parentTitle = (String) row[18];
        String parentSlug = (String) row[19];

        Boolean isLiked = null;
        Boolean isDisliked = null;
        Boolean isSaved = null;
        Boolean isOwner = null;

        if (hasCurrentUser && row.length > 20) {
            isLiked = row[20] != null ? (Boolean) row[20] : null;
            isDisliked = row[21] != null ? (Boolean) row[21] : null;
            isSaved = row[22] != null ? (Boolean) row[22] : null;
            isOwner = row[23] != null ? (Boolean) row[23] : null;
        }

        return new FeedItemResponse(
                id,
                type,
                title,
                slug,
                content,
                authorId,
                authorName,
                authorUserName,
                authorProfileImage,
                createdAt,
                likesCount,
                dislikesCount,
                commentsCount,
                savesCount,
                coverImage,
                tags,
                parentType,
                parentAuthorUserName,
                parentTitle,
                parentSlug,
                isLiked,
                isDisliked,
                isSaved,
                isOwner
        );
    }
}
