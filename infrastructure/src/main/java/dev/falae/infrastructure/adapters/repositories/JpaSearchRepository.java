package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.SearchContentType;
import dev.falae.application.ports.dto.TagSearchPageResponse;
import dev.falae.application.ports.dto.TagSearchResultItem;
import dev.falae.application.ports.repositories.SearchRepository;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public class JpaSearchRepository implements SearchRepository {

    private final EntityManager entityManager;
    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;

    public JpaSearchRepository(EntityManager entityManager, AuthenticatedAuthorProvider authenticatedAuthorProvider) {
        this.entityManager = entityManager;
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
    }

    @Override
    public TagSearchPageResponse searchByTags(
            List<String> tags,
            SearchContentType type,
            FeedSortType sortType,
            int page,
            int size
    ) {
        AuthorEntity currentAuthor = authenticatedAuthorProvider.getCurrentAuthorOrNull();
        UUID currentAuthorId = currentAuthor != null ? currentAuthor.getId() : null;

        String orderBy = switch (sortType) {
            case RECENT -> "created_at DESC";
            case OLDEST -> "created_at ASC";
            case LIKES -> "likes_count DESC, created_at DESC";
        };

        String sql = buildSearchQuery(type, orderBy);
        String countSql = buildCountQuery(type);

        Query query = entityManager.createNativeQuery(sql);
        Query countQuery = entityManager.createNativeQuery(countSql);

        String tagsArray = "{" + String.join(",", tags) + "}";
        query.setParameter("tags", tagsArray);
        countQuery.setParameter("tags", tagsArray);

        query.setParameter("currentAuthorId", currentAuthorId != null ? currentAuthorId.toString() : null);
        query.setParameter("offset", page * size);
        query.setParameter("limit", size);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        long totalElements = ((Number) countQuery.getSingleResult()).longValue();

        List<TagSearchResultItem> items = results.stream()
                .map(row -> mapToSearchItem(row, currentAuthorId))
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;

        return new TagSearchPageResponse(
                items,
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );
    }

    private String buildSearchQuery(SearchContentType type, String orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (");

        boolean needsUnion = false;

        if (type == SearchContentType.ALL || type == SearchContentType.ARTICLE) {
            sb.append("""
                SELECT
                    a.id,
                    'ARTICLE' as type,
                    a.title,
                    a.slug,
                    a.description as content,
                    a.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    a.created_at,
                    a.likes_count,
                    a.cover_image,
                    a.tags,
                    (SELECT COUNT(*) > 0 FROM clients_liked_article cla WHERE cla.article_id = a.id AND cla.client_id = CAST(:currentAuthorId AS UUID)) as is_liked,
                    NULL as parent_type,
                    NULL as parent_author_user_name,
                    NULL as parent_slug
                FROM articles a
                LEFT JOIN authors au ON a.author_id = au.id
                WHERE a.tags && CAST(:tags AS TEXT[])
            """);
            needsUnion = true;
        }

        if (type == SearchContentType.ALL || type == SearchContentType.TOPIC) {
            if (needsUnion) {
                sb.append(" UNION ALL ");
            }
            sb.append("""
                SELECT
                    t.id,
                    'TOPIC' as type,
                    t.title,
                    t.slug,
                    t.topic_content as content,
                    t.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    t.created_at,
                    t.likes_count,
                    NULL as cover_image,
                    t.tags,
                    (SELECT COUNT(*) > 0 FROM authors_liked_topic alt WHERE alt.topic_id = t.id AND alt.author_id = CAST(:currentAuthorId AS UUID)) as is_liked,
                    NULL as parent_type,
                    NULL as parent_author_user_name,
                    NULL as parent_slug
                FROM topics t
                LEFT JOIN authors au ON t.author_id = au.id
                WHERE t.tags && CAST(:tags AS TEXT[])
            """);
            needsUnion = true;
        }

        if (type == SearchContentType.ALL || type == SearchContentType.COMMENT) {
            if (needsUnion) {
                sb.append(" UNION ALL ");
            }
            sb.append("""
                SELECT
                    c.id,
                    'COMMENT' as type,
                    NULL as title,
                    NULL as slug,
                    c.comment_content as content,
                    c.author_id,
                    au.name as author_name,
                    au.user_name as author_user_name,
                    c.created_at,
                    c.likes as likes_count,
                    NULL as cover_image,
                    c.tags,
                    (SELECT COUNT(*) > 0 FROM comment_likes cl WHERE cl.comment_id = c.id AND cl.author_id = CAST(:currentAuthorId AS UUID)) as is_liked,
                    CASE WHEN c.article_id IS NOT NULL THEN 'ARTICLE' ELSE 'TOPIC' END as parent_type,
                    COALESCE(art_au.user_name, top_au.user_name) as parent_author_user_name,
                    COALESCE(art.slug, top.slug) as parent_slug
                FROM comments c
                LEFT JOIN authors au ON c.author_id = au.id
                LEFT JOIN articles art ON c.article_id = art.id
                LEFT JOIN authors art_au ON art.author_id = art_au.id
                LEFT JOIN topics top ON c.topic_id = top.id
                LEFT JOIN authors top_au ON top.author_id = top_au.id
                WHERE c.deleted = false AND c.tags && CAST(:tags AS TEXT[])
            """);
        }

        sb.append(") AS search_results ORDER BY ");
        sb.append(orderBy);
        sb.append(" OFFSET :offset LIMIT :limit");

        return sb.toString();
    }

    private String buildCountQuery(SearchContentType type) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM (");

        boolean needsUnion = false;

        if (type == SearchContentType.ALL || type == SearchContentType.ARTICLE) {
            sb.append("SELECT a.id FROM articles a WHERE a.tags && CAST(:tags AS TEXT[])");
            needsUnion = true;
        }

        if (type == SearchContentType.ALL || type == SearchContentType.TOPIC) {
            if (needsUnion) {
                sb.append(" UNION ALL ");
            }
            sb.append("SELECT t.id FROM topics t WHERE t.tags && CAST(:tags AS TEXT[])");
            needsUnion = true;
        }

        if (type == SearchContentType.ALL || type == SearchContentType.COMMENT) {
            if (needsUnion) {
                sb.append(" UNION ALL ");
            }
            sb.append("SELECT c.id FROM comments c WHERE c.deleted = false AND c.tags && CAST(:tags AS TEXT[])");
        }

        sb.append(") AS count_results");

        return sb.toString();
    }

    private TagSearchResultItem mapToSearchItem(Object[] row, UUID currentAuthorId) {
        UUID id = row[0] instanceof UUID ? (UUID) row[0] : UUID.fromString(row[0].toString());
        SearchContentType type = SearchContentType.valueOf((String) row[1]);
        String title = (String) row[2];
        String slug = (String) row[3];
        String content = (String) row[4];
        UUID authorId = row[5] != null ? (row[5] instanceof UUID ? (UUID) row[5] : UUID.fromString(row[5].toString())) : null;
        String authorName = (String) row[6];
        String authorUserName = (String) row[7];
        LocalDateTime createdAt = row[8] instanceof Timestamp ? ((Timestamp) row[8]).toLocalDateTime() : (LocalDateTime) row[8];
        int likesCount = row[9] != null ? ((Number) row[9]).intValue() : 0;
        String coverImage = (String) row[10];

        List<String> tags = null;
        if (row[11] != null) {
            if (row[11] instanceof String[]) {
                tags = Arrays.asList((String[]) row[11]);
            } else if (row[11] instanceof String) {
                String tagsStr = (String) row[11];
                if (tagsStr.startsWith("{") && tagsStr.endsWith("}")) {
                    tagsStr = tagsStr.substring(1, tagsStr.length() - 1);
                    tags = tagsStr.isEmpty() ? List.of() : Arrays.asList(tagsStr.split(","));
                }
            }
        }

        boolean isLiked = currentAuthorId != null && row[12] != null && (Boolean) row[12];

        SearchContentType parentType = row[13] != null ? SearchContentType.valueOf((String) row[13]) : null;
        String parentAuthorUserName = (String) row[14];
        String parentSlug = (String) row[15];

        return new TagSearchResultItem(
                id,
                type,
                title,
                slug,
                content,
                authorId,
                authorName,
                authorUserName,
                createdAt,
                likesCount,
                isLiked,
                coverImage,
                tags,
                parentType,
                parentAuthorUserName,
                parentSlug
        );
    }
}
