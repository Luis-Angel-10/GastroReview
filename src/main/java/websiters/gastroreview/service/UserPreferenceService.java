package websiters.gastroreview.service;

import websiters.gastroreview.dto.UserPreferenceRequest;
import websiters.gastroreview.dto.UserPreferenceResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.UserPreference;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.UserPreferenceRepository;
import websiters.gastroreview.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository repo;
    private final UserRepository usersRepo;

    public UserPreferenceService(UserPreferenceRepository repo, UserRepository usersRepo) {
        this.repo = repo;
        this.usersRepo = usersRepo;
    }

    @Transactional(readOnly = true)
    public Page<UserPreferenceResponse> list(UUID userId, String prefKey, Pageable pageable) {
        Page<UserPreference> preferences;

        if (userId != null && prefKey != null && !prefKey.isBlank()) {
            preferences = repo.findByUser_IdAndPrefKeyContainingIgnoreCase(userId, prefKey.trim(), pageable);
        } else if (userId != null) {
            preferences = repo.findByUser_Id(userId, pageable);
        } else if (prefKey != null && !prefKey.isBlank()) {
            preferences = repo.findByPrefKeyContainingIgnoreCase(prefKey.trim(), pageable);
        } else {
            preferences = repo.findAll(pageable);
        }

        return preferences.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public UserPreferenceResponse get(UUID id) {
        UserPreference p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));
        return Mappers.toResponse(p);
    }

    @Transactional
    public UserPreferenceResponse create(UserPreferenceRequest in) {
        UUID userId = in.getUser_Id();

        if (repo.existsByUser_IdAndPrefKeyIgnoreCase(userId, in.getPref_key())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Preference key already exists for user");
        }

        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserPreference p = UserPreference.builder()
                .user(user)
                .prefKey(in.getPref_key())
                .value(in.getValue())
                .build();

        try {
            p = repo.save(p);
            return Mappers.toResponse(p);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Preference key already exists for user");
        }
    }

    @Transactional
    public UserPreferenceResponse update(UUID id, UserPreferenceRequest in) {
        UserPreference p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));

        UUID targetUserId = (in.getUser_Id() != null) ? in.getUser_Id() : p.getUser().getId();

        if (in.getPref_key() != null && !in.getPref_key().equalsIgnoreCase(p.getPrefKey())) {
            if (repo.existsByUser_IdAndPrefKeyIgnoreCaseAndIdNot(targetUserId, in.getPref_key(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Preference key already exists for user");
            }
            p.setPrefKey(in.getPref_key());
        }

        if (in.getUser_Id() != null) {
            User newUser = usersRepo.findById(in.getUser_Id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            p.setUser(newUser);
        }

        if (in.getValue() != null) p.setValue(in.getValue());

        try {
            p = repo.save(p);
            return Mappers.toResponse(p);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Preference key already exists for user");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found");
        }
        repo.deleteById(id);
    }
}
