package dev.falae.infrastructure;

import dev.falae.infrastructure.adapters.repositories.entities.*;
import dev.falae.infrastructure.adapters.repositories.jpa.*;
import dev.falae.infrastructure.adapters.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TestDataLoader {

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    @Autowired
    private AdminJpaRepository adminJpaRepository;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    @Autowired
    private TopicJpaRepository topicJpaRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private ForumConfigJpaRepository forumConfigJpaRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthorEntity author1;
    private AuthorEntity author2;
    private AuthorEntity author3;
    private AdminEntity admin1;
    private AdminEntity admin2;
    private ArticleEntity article1;
    private ArticleEntity article2;
    private TopicEntity topic1;
    private TopicEntity topic2;
    private CommentEntity articleComment1;
    private CommentEntity articleComment2;
    private CommentEntity articleComment1Reply;
    private CommentEntity articleComment3;
    private CommentEntity articleComment4;
    private CommentEntity articleComment3Reply;
    private CommentEntity topicComment1;
    private CommentEntity topicComment2;
    private CommentEntity topicComment1Reply;
    private CommentEntity topicComment3;
    private CommentEntity topicComment4;
    private CommentEntity topicComment3Reply;

    public void loadTestData() {
        createForumConfig();
        createAuthors();
        createAdmins();
        createArticles();
        createTopics();
        createArticleComments();
        createTopicComments();
    }

    private void createForumConfig() {
        ForumConfigEntity config = new ForumConfigEntity();
        config.setId(UUID.randomUUID());
        config.setAmountCoinsStart(10);
        config.setCoinsFirstArticle(50);
        config.setCoinsFirstComment(10);
        config.setCoinsFirstTopic(30);
        config.setCoinsPerTopic(5);
        config.setCoinsPerComment(2);
        config.setCoinsPerArticle(10);
        config.setStoreUnlocked(false);
        config.setUserTitleUnlocked(true);
        config.setArticleCreationUnlocked(true);
        config.setTopicCreationUnlocked(true);
        config.setCommentUnlocked(true);
        forumConfigJpaRepository.save(config);
    }

    private void createAuthors() {
        author1 = new AuthorEntity();
        author1.setId(UUID.randomUUID());
        author1.setEmail("author1@test.com");
        author1.setPassword(passwordEncoder.encode("password123"));
        author1.setName("Author One");
        author1.setUserName("author-one");
        author1.setGitHub("https://github.com/author1");
        author1.setBio("Bio of author one");
        author1.setBugCoins(100);
        author1.setGoogleLogin(false);
        author1.setTitle("Senior Developer");
        author1 = authorJpaRepository.save(author1);

        author2 = new AuthorEntity();
        author2.setId(UUID.randomUUID());
        author2.setEmail("author2@test.com");
        author2.setPassword(passwordEncoder.encode("password123"));
        author2.setName("Author Two");
        author2.setUserName("author-two");
        author2.setGitHub("https://github.com/author2");
        author2.setBio("Bio of author two");
        author2.setBugCoins(200);
        author2.setGoogleLogin(false);
        author2.setTitle("Tech Lead");
        author2 = authorJpaRepository.save(author2);

        author3 = new AuthorEntity();
        author3.setId(UUID.randomUUID());
        author3.setEmail("author3@test.com");
        author3.setPassword(passwordEncoder.encode("password123"));
        author3.setName("Author Three");
        author3.setUserName("author-three");
        author3.setGitHub("https://github.com/author3");
        author3.setBio("Bio of author three - no dependencies");
        author3.setBugCoins(50);
        author3.setGoogleLogin(false);
        author3.setTitle("Junior Developer");
        author3 = authorJpaRepository.save(author3);
    }

    private void createAdmins() {
        admin1 = new AdminEntity();
        admin1.setId(UUID.randomUUID());
        admin1.setEmail("admin1@test.com");
        admin1.setPassword(passwordEncoder.encode("admin123"));
        admin1.setName("Admin One");
        admin1 = adminJpaRepository.save(admin1);

        admin2 = new AdminEntity();
        admin2.setId(UUID.randomUUID());
        admin2.setEmail("admin2@test.com");
        admin2.setPassword(passwordEncoder.encode("admin123"));
        admin2.setName("Admin Two");
        admin2 = adminJpaRepository.save(admin2);
    }

    private void createArticles() {
        article1 = new ArticleEntity();
        article1.setId(UUID.randomUUID());
        article1.setAuthor(author1);
        article1.setTitle("First Article Title");
        article1.setSlug("first-article-title");
        article1.setDescription("Description of the first article");
        article1.setMarkdown(true);
        article1.setTags(List.of("java", "spring"));
        article1.setImagePaths(new ArrayList<>());
        article1 = articleJpaRepository.save(article1);

        article2 = new ArticleEntity();
        article2.setId(UUID.randomUUID());
        article2.setAuthor(author2);
        article2.setTitle("Second Article Title");
        article2.setSlug("second-article-title");
        article2.setDescription("Description of the second article");
        article2.setMarkdown(false);
        article2.setTags(List.of("kotlin", "android"));
        article2.setImagePaths(new ArrayList<>());
        article2 = articleJpaRepository.save(article2);
    }

    private void createTopics() {
        topic1 = new TopicEntity(author1, "First Topic Title", "first-topic-title", "Content of the first topic", List.of("java", "backend"));
        topic1.setId(UUID.randomUUID());
        topic1 = topicJpaRepository.save(topic1);

        topic2 = new TopicEntity(author2, "Second Topic Title", "second-topic-title", "Content of the second topic", List.of("kotlin", "mobile"));
        topic2.setId(UUID.randomUUID());
        topic2 = topicJpaRepository.save(topic2);
    }

    private void createArticleComments() {
        articleComment1 = new CommentEntity();
        articleComment1.setId(UUID.randomUUID());
        articleComment1.setAuthor(author2);
        articleComment1.setArticle(article1);
        articleComment1.setCommentContent("First comment on article 1");
        articleComment1.setDepth(0);
        articleComment1.setPath("/" + articleComment1.getId());
        articleComment1 = commentJpaRepository.save(articleComment1);

        articleComment2 = new CommentEntity();
        articleComment2.setId(UUID.randomUUID());
        articleComment2.setAuthor(author1);
        articleComment2.setArticle(article1);
        articleComment2.setCommentContent("Second comment on article 1");
        articleComment2.setDepth(0);
        articleComment2.setPath("/" + articleComment2.getId());
        articleComment2 = commentJpaRepository.save(articleComment2);

        articleComment1Reply = new CommentEntity();
        articleComment1Reply.setId(UUID.randomUUID());
        articleComment1Reply.setAuthor(author1);
        articleComment1Reply.setArticle(article1);
        articleComment1Reply.setParent(articleComment1);
        articleComment1Reply.setCommentContent("Reply to first comment on article 1");
        articleComment1Reply.setDepth(1);
        articleComment1Reply.setPath(articleComment1.getPath() + "/" + articleComment1Reply.getId());
        articleComment1Reply = commentJpaRepository.save(articleComment1Reply);

        articleComment3 = new CommentEntity();
        articleComment3.setId(UUID.randomUUID());
        articleComment3.setAuthor(author1);
        articleComment3.setArticle(article2);
        articleComment3.setCommentContent("First comment on article 2");
        articleComment3.setDepth(0);
        articleComment3.setPath("/" + articleComment3.getId());
        articleComment3 = commentJpaRepository.save(articleComment3);

        articleComment4 = new CommentEntity();
        articleComment4.setId(UUID.randomUUID());
        articleComment4.setAuthor(author2);
        articleComment4.setArticle(article2);
        articleComment4.setCommentContent("Second comment on article 2");
        articleComment4.setDepth(0);
        articleComment4.setPath("/" + articleComment4.getId());
        articleComment4 = commentJpaRepository.save(articleComment4);

        articleComment3Reply = new CommentEntity();
        articleComment3Reply.setId(UUID.randomUUID());
        articleComment3Reply.setAuthor(author2);
        articleComment3Reply.setArticle(article2);
        articleComment3Reply.setParent(articleComment3);
        articleComment3Reply.setCommentContent("Reply to first comment on article 2");
        articleComment3Reply.setDepth(1);
        articleComment3Reply.setPath(articleComment3.getPath() + "/" + articleComment3Reply.getId());
        articleComment3Reply = commentJpaRepository.save(articleComment3Reply);
    }

    private void createTopicComments() {
        topicComment1 = new CommentEntity();
        topicComment1.setId(UUID.randomUUID());
        topicComment1.setAuthor(author2);
        topicComment1.setTopic(topic1);
        topicComment1.setCommentContent("First comment on topic 1");
        topicComment1.setDepth(0);
        topicComment1.setPath("/" + topicComment1.getId());
        topicComment1 = commentJpaRepository.save(topicComment1);

        topicComment2 = new CommentEntity();
        topicComment2.setId(UUID.randomUUID());
        topicComment2.setAuthor(author1);
        topicComment2.setTopic(topic1);
        topicComment2.setCommentContent("Second comment on topic 1");
        topicComment2.setDepth(0);
        topicComment2.setPath("/" + topicComment2.getId());
        topicComment2 = commentJpaRepository.save(topicComment2);

        topicComment1Reply = new CommentEntity();
        topicComment1Reply.setId(UUID.randomUUID());
        topicComment1Reply.setAuthor(author1);
        topicComment1Reply.setTopic(topic1);
        topicComment1Reply.setParent(topicComment1);
        topicComment1Reply.setCommentContent("Reply to first comment on topic 1");
        topicComment1Reply.setDepth(1);
        topicComment1Reply.setPath(topicComment1.getPath() + "/" + topicComment1Reply.getId());
        topicComment1Reply = commentJpaRepository.save(topicComment1Reply);

        topicComment3 = new CommentEntity();
        topicComment3.setId(UUID.randomUUID());
        topicComment3.setAuthor(author1);
        topicComment3.setTopic(topic2);
        topicComment3.setCommentContent("First comment on topic 2");
        topicComment3.setDepth(0);
        topicComment3.setPath("/" + topicComment3.getId());
        topicComment3 = commentJpaRepository.save(topicComment3);

        topicComment4 = new CommentEntity();
        topicComment4.setId(UUID.randomUUID());
        topicComment4.setAuthor(author2);
        topicComment4.setTopic(topic2);
        topicComment4.setCommentContent("Second comment on topic 2");
        topicComment4.setDepth(0);
        topicComment4.setPath("/" + topicComment4.getId());
        topicComment4 = commentJpaRepository.save(topicComment4);

        topicComment3Reply = new CommentEntity();
        topicComment3Reply.setId(UUID.randomUUID());
        topicComment3Reply.setAuthor(author2);
        topicComment3Reply.setTopic(topic2);
        topicComment3Reply.setParent(topicComment3);
        topicComment3Reply.setCommentContent("Reply to first comment on topic 2");
        topicComment3Reply.setDepth(1);
        topicComment3Reply.setPath(topicComment3.getPath() + "/" + topicComment3Reply.getId());
        topicComment3Reply = commentJpaRepository.save(topicComment3Reply);
    }

    public String generateAuthorToken(AuthorEntity author) {
        return jwtTokenService.generateToken(author.getId(), author.getEmail(), "AUTHOR");
    }

    public String generateAdminToken(AdminEntity admin) {
        return jwtTokenService.generateToken(admin.getId(), admin.getEmail(), "ADMIN");
    }

    public AuthorEntity getAuthor1() {
        return author1;
    }

    public AuthorEntity getAuthor2() {
        return author2;
    }

    public AuthorEntity getAuthor3() {
        return author3;
    }

    public AdminEntity getAdmin1() {
        return admin1;
    }

    public AdminEntity getAdmin2() {
        return admin2;
    }

    public ArticleEntity getArticle1() {
        return article1;
    }

    public ArticleEntity getArticle2() {
        return article2;
    }

    public TopicEntity getTopic1() {
        return topic1;
    }

    public TopicEntity getTopic2() {
        return topic2;
    }

    public CommentEntity getArticleComment1() {
        return articleComment1;
    }

    public CommentEntity getArticleComment2() {
        return articleComment2;
    }

    public CommentEntity getArticleComment1Reply() {
        return articleComment1Reply;
    }

    public CommentEntity getArticleComment3() {
        return articleComment3;
    }

    public CommentEntity getArticleComment4() {
        return articleComment4;
    }

    public CommentEntity getArticleComment3Reply() {
        return articleComment3Reply;
    }

    public CommentEntity getTopicComment1() {
        return topicComment1;
    }

    public CommentEntity getTopicComment2() {
        return topicComment2;
    }

    public CommentEntity getTopicComment1Reply() {
        return topicComment1Reply;
    }

    public CommentEntity getTopicComment3() {
        return topicComment3;
    }

    public CommentEntity getTopicComment4() {
        return topicComment4;
    }

    public CommentEntity getTopicComment3Reply() {
        return topicComment3Reply;
    }
}
