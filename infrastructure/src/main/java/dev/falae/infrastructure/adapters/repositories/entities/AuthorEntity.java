package dev.falae.infrastructure.adapters.repositories.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "authors", schema = "public")
public class AuthorEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String name;

    @Column(nullable = false, unique = true, length = 40)
    @Pattern(
            regexp = "^[a-z0-9]+(?:[-_][a-z0-9]+)*$",
            message = "O userName deve estar em formato de slug: apenas letras minúsculas, números, hífens e underscores."
    )
    @Size(max = 40, message = "O userName deve ter no máximo 40 caracteres.")
    private String userName;

    @Pattern(
            regexp = "^https://github\\.com/[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?$",
            message = "O gitHub deve ser uma URL válida de perfil do GitHub (ex: https://github.com/usuario)."
    )
    private String gitHub;

    private String profileImageUrl;

    @Column(length = 500)
    @Size(max = 500, message = "A bio deve ter no máximo 500 caracteres.")
    private String bio;

    private int bugCoins;

    private boolean googleLogin;

    private String title;

    private boolean emailVerified;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    @JsonBackReference
    private List<ArticleEntity> articles;

// ---------- Getters e Setters ----------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGitHub() {
        return gitHub;
    }

    public void setGitHub(String gitHub) {
        this.gitHub = gitHub;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getBugCoins() {
        return bugCoins;
    }

    public void setBugCoins(int bugCoins) {
        this.bugCoins = bugCoins;
    }

    public boolean isGoogleLogin() {
        return googleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        this.googleLogin = googleLogin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public List<ArticleEntity> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleEntity> articles) {
        this.articles = articles;
    }
}
