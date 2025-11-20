package websiters.gastroreview.service;

import websiters.gastroreview.dto.NotificationRequest;
import websiters.gastroreview.dto.NotificationResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Notification;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.NotificationRepository;
import websiters.gastroreview.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepo;


    @Transactional(readOnly = true)
    public Page<NotificationResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findByUser(UUID userId, Pageable pageable) {

        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return repo.findByUserId(userId, pageable)
                .map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public NotificationResponse get(UUID id) {
        Notification n = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        return Mappers.toResponse(n);
    }

    @Transactional
    public NotificationResponse create(NotificationRequest in) {

        Notification n = new Notification();

        User user = userRepo.findById(in.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        n.setUser(user);

        n.setType(in.getType());
        n.setMessage(in.getMessage());
        n.setRead(false);

        n.setReferenceId(in.getReferenceId());

        Map<String, Object> metadata = in.getMetadata();
        n.setMetadata(metadata != null ? metadata : Map.of());

        try {
            n = repo.save(n);
            return Mappers.toResponse(n);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification data");
        }
    }

    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        Notification n = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        n.setRead(true);
        n = repo.save(n);

        return Mappers.toResponse(n);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        repo.deleteById(id);
    }
}
