package websiters.gastroreview.repository;

import websiters.gastroreview.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    Page<UserProfile> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<UserProfile> findByActive(Boolean active, Pageable pageable);
    Page<UserProfile> findByBioContainingIgnoreCase(String keyword, Pageable pageable);
}
