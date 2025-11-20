package websiters.gastroreview.repository;

import websiters.gastroreview.model.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(@NotBlank String name, UUID id);

    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
