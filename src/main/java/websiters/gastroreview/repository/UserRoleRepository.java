package websiters.gastroreview.repository;

import websiters.gastroreview.model.UserRole;
import websiters.gastroreview.model.UserRoleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    Page<UserRole> findById_UserId(UUID userId, Pageable pageable);
    Page<UserRole> findById_RoleId(UUID roleId, Pageable pageable);
    Page<UserRole> findById_UserIdAndId_RoleId(UUID userId, UUID roleId, Pageable pageable);

    boolean existsById(UserRoleId id);
}
