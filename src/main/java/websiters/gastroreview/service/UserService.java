package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.UserRequest;
import websiters.gastroreview.dto.UserResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Role;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.RoleRepository;
import websiters.gastroreview.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> list(String email, Pageable pageable) {
        Page<User> users = (email != null && !email.isBlank())
                ? repo.findByEmailContainingIgnoreCase(email.trim(), pageable)
                : repo.findAll(pageable);
        return users.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        User u = repo.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return Mappers.toResponse(u);
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return Mappers.toResponse(u);
    }

    @Transactional
    public UserResponse create(UserRequest in) {

        if (in == null || in.getEmail() == null || in.getEmail().isBlank()
                || in.getPassword() == null || in.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        String normalizedEmail = in.getEmail().trim().toLowerCase();

        if (repo.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        String roleName = (in.getRole() != null && !in.getRole().isBlank())
                ? in.getRole().trim().toUpperCase()
                : "USER";

        Role role = roleRepo.findAll().stream()
                .filter(r -> r.getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName)
                );

        String encodedPassword = passwordEncoder.encode(in.getPassword());

        User u = User.builder()
                .email(normalizedEmail)
                .password(encodedPassword)
                .roles(Set.of(role))
                .build();

        try {
            u = repo.save(u);
            return Mappers.toResponse(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest in) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (in.getEmail() != null && !in.getEmail().isBlank()) {
            u.setEmail(in.getEmail().trim().toLowerCase());
        }

        if (in.getPassword() != null && !in.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(in.getPassword()));
        }

        try {
            u = repo.save(u);
            return Mappers.toResponse(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse authenticate(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = repo.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return Mappers.toResponse(user);
    }
}
