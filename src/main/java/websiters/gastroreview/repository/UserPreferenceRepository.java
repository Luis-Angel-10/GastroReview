package websiters.gastroreview.repository;

import websiters.gastroreview.model.UserPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.User;

import java.util.UUID;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {

    boolean existsByUserAndPrefKey(User user, String prefKey);

    boolean existsByUser_IdAndPrefKeyIgnoreCase(UUID userId, String key);

    boolean existsByUser_IdAndPrefKeyIgnoreCaseAndIdNot(UUID targetUserId, String key, UUID id);

    Page<UserPreference> findByUser_Id(UUID userId, Pageable pageable);
    Page<UserPreference> findByPrefKeyContainingIgnoreCase(String prefKey, Pageable pageable);
    Page<UserPreference> findByUser_IdAndPrefKeyContainingIgnoreCase(UUID userId, String prefKey, Pageable pageable);
}
