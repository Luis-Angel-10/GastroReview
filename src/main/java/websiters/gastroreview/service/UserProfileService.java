package websiters.gastroreview.service;

import websiters.gastroreview.dto.UserProfileRequest;
import websiters.gastroreview.dto.UserProfileResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.UserProfile;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.UserProfileRepository;
import websiters.gastroreview.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository repo;
    private final UserRepository usersRepo;

    public UserProfileService(UserProfileRepository repo, UserRepository usersRepo) {
        this.repo = repo;
        this.usersRepo = usersRepo;
    }

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> list(String name, String bio, Boolean active, Pageable pageable) {
        Page<UserProfile> profiles;

        if (name != null && !name.isBlank()) {
            profiles = repo.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (bio != null && !bio.isBlank()) {
            profiles = repo.findByBioContainingIgnoreCase(bio.trim(), pageable);
        } else if (active != null) {
            profiles = repo.findByActive(active, pageable);
        } else {
            profiles = repo.findAll(pageable);
        }

        return profiles.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse get(UUID userId) {
        UserProfile p = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
        return Mappers.toResponse(p);
    }

    @Transactional
    public UserProfileResponse upsert(UserProfileRequest in) {
        User user = usersRepo.findById(in.getUser_Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));

        UserProfile p = repo.findById(in.getUser_Id()).orElse(new UserProfile());
        p.setUser(user);

        if (in.getPhoto_Url() != null) p.setPhotoUrl(in.getPhoto_Url());
        if (in.getName() != null) p.setName(in.getName());
        if (in.getActive() != null) p.setActive(in.getActive());
        if (in.getBio() != null) p.setBio(in.getBio());

        p = repo.save(p);
        return Mappers.toResponse(p);
    }

    @Transactional
    public UserProfileResponse update(UUID userId, UserProfileRequest in) {
        UserProfile p = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        if (in.getPhoto_Url() != null) p.setPhotoUrl(in.getPhoto_Url());
        if (in.getName() != null) p.setName(in.getName());
        if (in.getActive() != null) p.setActive(in.getActive());
        if (in.getBio() != null) p.setBio(in.getBio());

        p = repo.save(p);
        return Mappers.toResponse(p);
    }

    @Transactional
    public void delete(UUID userId) {
        if (!repo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found");
        }
        repo.deleteById(userId);
    }
}
