package websiters.gastroreview.service;

import websiters.gastroreview.dto.UserRoleRequest;
import websiters.gastroreview.dto.UserRoleResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Role;
import websiters.gastroreview.model.UserRole;
import websiters.gastroreview.model.UserRoleId;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.RoleRepository;
import websiters.gastroreview.repository.UserRoleRepository;
import websiters.gastroreview.repository.UserRepository;
import jakarta.validation.Valid;
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
public class UserRoleService {

    private final UserRoleRepository repo;
    private final UserRepository usersRepo;
    private final RoleRepository roleRepo;

    public UserRoleService(UserRoleRepository repo, UserRepository usersRepo, RoleRepository roleRepo) {
        this.repo = repo;
        this.usersRepo = usersRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional(readOnly = true)
    public Page<UserRoleResponse> list(UUID userId, UUID roleId, Pageable pageable) {
        Page<UserRole> assignments;

        if (userId != null && roleId != null) {
            assignments = repo.findById_UserIdAndId_RoleId(userId, roleId, pageable);
        } else if (userId != null) {
            assignments = repo.findById_UserId(userId, pageable);
        } else if (roleId != null) {
            assignments = repo.findById_RoleId(roleId, pageable);
        } else {
            assignments = repo.findAll(pageable);
        }

        return assignments.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public UserRoleResponse get(UUID userId, UUID roleId) {
        var id = new UserRoleId(userId, roleId);
        var ur = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserRole not found"));
        return Mappers.toResponse(ur);
    }

    @Transactional
    public UserRoleResponse create(@Valid UserRoleRequest in) {
        if (in.getUser_Id() == null || in.getRole_Id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user_Id and roleId are required");
        }

        User user = usersRepo.findById(in.getUser_Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Role role = roleRepo.findById(in.getRole_Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        var id = new UserRoleId(in.getUser_Id(), in.getRole_Id());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has this role");
        }

        var entity = UserRole.builder()
                .id(id)
                .user(user)
                .role(role)
                .build();

        try {
            var saved = repo.saveAndFlush(entity);
            return Mappers.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has this role");
        }
    }

    @Transactional
    public UserRoleResponse update(UUID userId, UUID roleId, @Valid UserRoleRequest in) {
        if (!Objects.equals(userId, in.getUser_Id()) || !Objects.equals(roleId, in.getRole_Id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change user_Id or roleId");
        }

        var id = new UserRoleId(userId, roleId);
        var existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserRole not found"));
        return Mappers.toResponse(existing);
    }

    @Transactional
    public void delete(UUID userId, UUID roleId) {
        var id = new UserRoleId(userId, roleId);
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserRole not found");
        }
        try {
            repo.deleteById(id);
            repo.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete: role in use");
        }
    }
}
