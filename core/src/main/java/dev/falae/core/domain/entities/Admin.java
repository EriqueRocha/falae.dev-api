package dev.falae.core.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Admin {
    private UUID id;
    private String email;
    private String password;
    private String name;

    public Admin() {}

    public Admin(String email, String password, String name) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Admin(UUID id, String email, String password, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getRole() {
        return "ADMIN";
    }

//------------- getters and Setters -----------------------

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(id, admin.id) && Objects.equals(email, admin.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
