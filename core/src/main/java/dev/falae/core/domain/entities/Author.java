package dev.falae.core.domain.entities;

import java.util.Objects;
import java.util.UUID;

public class Author {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String userName;
    private String gitHub;
    private String profileImageUrl;
    private String bio;
    private int bugCoins;
    private boolean googleLogin;
    private String title;
    private boolean emailVerified;

    public Author() {}

    public Author(String email, String password, String name, String userName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
    }

    public Author(UUID id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Author(UUID id, String email, String password, String name, String userName, String gitHub, String profileImageUrl, String bio, int bugCoins, boolean googleLogin, String title, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.gitHub = gitHub;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.bugCoins = bugCoins;
        this.googleLogin = googleLogin;
        this.title = title;
        this.emailVerified = emailVerified;
    }

    public String getRole() {
        return "AUTHOR";
    }

//----------------- getters and Setters ---------------------------

    public UUID getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id) && Objects.equals(email, author.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
