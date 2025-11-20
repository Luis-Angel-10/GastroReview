package websiters.gastroreview.service;

import websiters.gastroreview.dto.RoleRequest;
import websiters.gastroreview.dto.RoleResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Role;
import websiters.gastroreview.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository repo;

    public RoleService(RoleRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<RoleResponse> list(String name, Pageable pageable) {
        Page<Role> roles;
        if (name == null || name.isBlank()) {
            roles = repo.findAll(pageable);
        } else {
            roles = repo.findByNameContainingIgnoreCase(name, pageable);
        }
        return roles.map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        Role r = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        return Mappers.toResponse(r);
    }

    @Transactional
    public RoleResponse create(RoleRequest in) {
        if (repo.existsByNameIgnoreCase(in.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
        }

        Role r = Role.builder()
                .name(in.getName())
                .description(in.getDescription())
                .build();

        try {
            r = repo.save(r);
            return Mappers.toResponse(r);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
        }
    }

    @Transactional
    public RoleResponse update(UUID id, RoleRequest in) {
        Role r = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if (in.getName() != null && !in.getName().equalsIgnoreCase(r.getName())) {
            if (repo.existsByNameIgnoreCaseAndIdNot(in.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
            }
            r.setName(in.getName());
        }

        if (in.getDescription() != null) r.setDescription(in.getDescription());

        try {
            r = repo.save(r);
            return Mappers.toResponse(r);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        repo.deleteById(id);
    }
}
