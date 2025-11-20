package websiters.gastroreview.service;

import websiters.gastroreview.dto.UserAchievementRequest;
import websiters.gastroreview.dto.UserAchievementResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.UserAchievement;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.UserAchievementRepository;
import websiters.gastroreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAchievementService {

    private final UserAchievementRepository repo;
    private final UserRepository usersRepo;

    @Transactional(readOnly = true)
    public Page<UserAchievementResponse> list(UUID userId, String badge, Pageable pageable) {
        Page<UserAchievement> achievements;

        if (userId != null && badge != null && !badge.isBlank()) {
            achievements = repo.findByUserIdAndBadgeContainingIgnoreCase(userId, badge.trim(), pageable);
        } else if (userId != null) {
            achievements = repo.findByUserId(userId, pageable);
        } else if (badge != null && !badge.isBlank()) {
            achievements = repo.findByBadgeContainingIgnoreCase(badge.trim(), pageable);
        } else {
            achievements = repo.findAll(pageable);
        }

        return achievements.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public UserAchievementResponse get(Long id) {
        UserAchievement a = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found"));
        return Mappers.toResponse(a);
    }

    @Transactional
    public UserAchievementResponse create(UserAchievementRequest in) {
        UUID userId = Objects.requireNonNull(in.getUser_Id(), "user_Id is required");
        String badge = Objects.requireNonNull(in.getBadge(), "badge is required").trim();

        if (repo.existsForUserAndBadge(userId, badge)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge already exists for user");
        }

        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserAchievement a = UserAchievement.builder()
                .user(user)
                .badge(badge)
                .level(in.getLevel() != null ? in.getLevel() : 1)
                .stars(in.getStars() != null ? in.getStars() : 0)
                .build();

        try {
            a = repo.saveAndFlush(a);
            return Mappers.toResponse(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge already exists for user");
        }
    }

    @Transactional
    public UserAchievementResponse update(Long id, UserAchievementRequest in) {
        UserAchievement a = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found"));

        UUID targetUserId = (in.getUser_Id() != null) ? in.getUser_Id() : a.getUser().getId();

        if (in.getBadge() != null) {
            String newBadge = in.getBadge().trim();
            String currentBadge = a.getBadge() != null ? a.getBadge().trim() : null;

            if (currentBadge == null || !currentBadge.equalsIgnoreCase(newBadge)) {
                if (repo.existsForUserAndBadgeExcluding(targetUserId, newBadge, id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge already exists for user");
                }
                a.setBadge(newBadge);
            }
        }

        if (in.getUser_Id() != null && !in.getUser_Id().equals(a.getUser().getId())) {
            User newUser = usersRepo.findById(in.getUser_Id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            a.setUser(newUser);
        }

        if (in.getLevel() != null) a.setLevel(in.getLevel());
        if (in.getStars() != null) a.setStars(in.getStars());

        try {
            a = repo.saveAndFlush(a);
            return Mappers.toResponse(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge already exists for user");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found");
        }
        repo.deleteById(id);
    }
}
