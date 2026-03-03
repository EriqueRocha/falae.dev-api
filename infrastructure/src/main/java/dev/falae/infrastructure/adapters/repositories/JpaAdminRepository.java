package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.repositories.AdminRepository;
import dev.falae.core.domain.entities.Admin;
import dev.falae.infrastructure.adapters.repositories.entities.AdminEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.AdminJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaAdminRepository implements AdminRepository {
    private final AdminJpaRepository jpaRepository;

    public JpaAdminRepository(AdminJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Admin findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain).orElseThrow(()-> new ResourceNotFoundException("Admin", email));
    }

    @Override
    public Admin save(Admin admin) {
        AdminEntity entity = toEntity(admin);
        AdminEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Admin findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain).orElseThrow(()-> new ResourceNotFoundException("Admin", id));
    }

    private Admin toDomain(AdminEntity entity) {
        return new Admin(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AdminEntity toEntity(Admin domain) {
        AdminEntity entity = new AdminEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setName(domain.getName());
        return entity;
    }
}
