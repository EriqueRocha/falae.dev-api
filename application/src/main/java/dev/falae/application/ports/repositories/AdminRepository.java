package dev.falae.application.ports.repositories;

import dev.falae.core.domain.entities.Admin;

import java.util.UUID;

public interface AdminRepository {
    Admin findByEmail(String email);
    Admin save(Admin admin);
    Admin findById(UUID id);
}
