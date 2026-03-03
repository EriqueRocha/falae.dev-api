package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.dto.AuthorProfileResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.core.domain.entities.Author;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.ArticleJpaRepository;
import dev.falae.infrastructure.adapters.repositories.jpa.AuthorJpaRepository;
import dev.falae.infrastructure.adapters.repositories.jpa.CommentJpaRepository;
import dev.falae.infrastructure.adapters.repositories.jpa.TopicJpaRepository;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAuthorRepository implements AuthorRepository {

    private final AuthorJpaRepository jpaRepository;
    private final ArticleJpaRepository articleJpaRepository;
    private final TopicJpaRepository topicJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;

    public JpaAuthorRepository(AuthorJpaRepository jpaRepository,
                               ArticleJpaRepository articleJpaRepository,
                               TopicJpaRepository topicJpaRepository,
                               CommentJpaRepository commentJpaRepository,
                               AuthenticatedAuthorProvider authenticatedAuthorProvider) {
        this.jpaRepository = jpaRepository;
        this.articleJpaRepository = articleJpaRepository;
        this.topicJpaRepository = topicJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
    }

    @Override
    public Author findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain).orElseThrow(()-> new ResourceNotFoundException("Author", email));
    }

    @Override
    public Optional<Author> findByEmailOptional(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Author findByUserName(String userName) {
        return jpaRepository.findByUserName(userName)
                .map(this::toDomain).orElseThrow(() -> new ResourceNotFoundException("Author", userName));
    }

    @Override
    public boolean existsByUserName(String userName) {
        return jpaRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByUserNameAndIdNot(String userName, UUID id) {
        return jpaRepository.existsByUserNameAndIdNot(userName, id);
    }

    @Override
    public AuthorProfileResponse findProfileByUserName(String userName) {
        AuthorEntity entity = jpaRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Author", userName));

        long articleCount = articleJpaRepository.countByAuthorId(entity.getId());
        long topicCount = topicJpaRepository.countByAuthorId(entity.getId());
        long commentCount = commentJpaRepository.countByAuthorId(entity.getId());

        return new AuthorProfileResponse(
                entity.getId(),
                entity.getName(),
                entity.getUserName(),
                entity.getGitHub(),
                entity.getProfileImageUrl(),
                entity.getBio(),
                entity.getBugCoins(),
                entity.getTitle(),
                articleCount,
                topicCount,
                commentCount,
                entity.isEmailVerified()
        );
    }

    @Override
    public Author save(Author author) {
        if (author.getId() != null && jpaRepository.existsById(author.getId())) {
            AuthorEntity entity = jpaRepository.findById(author.getId()).orElseThrow(()-> new ResourceNotFoundException("Author", author.getId()));
            updateFields(entity, author);
            return toDomain(jpaRepository.save(entity));
        }
        return toDomain(jpaRepository.save(toEntity(author)));
    }

    private void updateFields(AuthorEntity entity, Author domain) {
        if (domain.getName() != null) entity.setName(domain.getName());
        if (domain.getEmail() != null) entity.setEmail(domain.getEmail());
        if (domain.getPassword() != null) entity.setPassword(domain.getPassword());
        if (domain.getUserName() != null) entity.setUserName(domain.getUserName());
        if (domain.getGitHub() != null) entity.setGitHub(domain.getGitHub());
        if (domain.getBio() != null) entity.setBio(domain.getBio());
        if (domain.getProfileImageUrl() != null) entity.setProfileImageUrl(domain.getProfileImageUrl());
        entity.setEmailVerified(domain.isEmailVerified());
    }

    @Override
    public Author findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain).orElseThrow(()-> new ResourceNotFoundException("Author", id));
    }

    @Override
    public void deleteById(UUID id) {
        AuthorEntity author = jpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        jpaRepository.delete(author);
    }

    @Override
    public void addCoinsToCurrentAuthor(int coins) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        author.setBugCoins(author.getBugCoins() + coins);
        jpaRepository.save(author);
    }

    @Override
    public void removeCoinsFromCurrentAuthor(int coins) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        int newBalance = author.getBugCoins() - coins;
        author.setBugCoins(Math.max(0, newBalance));
        jpaRepository.save(author);
    }

    public Author toDomain(AuthorEntity entity) {
        return new Author(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getUserName(),
                entity.getGitHub(),
                entity.getProfileImageUrl(),
                entity.getBio(),
                entity.getBugCoins(),
                entity.isGoogleLogin(),
                entity.getTitle(),
                entity.isEmailVerified()
        );
    }

    private AuthorEntity toEntity(Author domain) {
        AuthorEntity entity = new AuthorEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setName(domain.getName());
        entity.setUserName(domain.getUserName());
        entity.setGitHub(domain.getGitHub());
        entity.setProfileImageUrl(domain.getProfileImageUrl());
        entity.setBio(domain.getBio());
        entity.setBugCoins(domain.getBugCoins());
        entity.setGoogleLogin(domain.isGoogleLogin());
        entity.setTitle(domain.getTitle());
        entity.setEmailVerified(domain.isEmailVerified());
        return entity;
    }
}