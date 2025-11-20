package websiters.gastroreview.service;

import websiters.gastroreview.dto.AlertRequest;
import websiters.gastroreview.dto.AlertResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Alert;
import websiters.gastroreview.model.Restaurant;
import websiters.gastroreview.model.Review;
import websiters.gastroreview.repository.AlertRepository;
import websiters.gastroreview.repository.RestaurantRepository;
import websiters.gastroreview.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository repo;
    private final RestaurantRepository restaurantRepo;
    private final ReviewRepository reviewRepo;

    @Transactional(readOnly = true)
    public Page<AlertResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {

        if (!restaurantRepo.existsById(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
        }

        return repo.findByRestaurantId(restaurantId, pageable)
                .map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> findByReview(UUID reviewId, Pageable pageable) {

        if (!reviewRepo.existsById(reviewId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        return repo.findByReviewId(reviewId, pageable)
                .map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public AlertResponse get(UUID id) {
        Alert a = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        return Mappers.toResponse(a);
    }

    @Transactional
    public AlertResponse create(AlertRequest in) {

        Alert alert = new Alert();

        alert.setType(in.getType());
        alert.setDetail(in.getDetail());

        if (in.getRestaurantId() != null) {
            Restaurant r = restaurantRepo.findById(in.getRestaurantId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
            alert.setRestaurant(r);
        }

        if (in.getReviewId() != null) {
            Review review = reviewRepo.findById(in.getReviewId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
            alert.setReview(review);
        }

        try {
            alert = repo.save(alert);
            return Mappers.toResponse(alert);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid alert data");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found");
        }
        repo.deleteById(id);
    }
}
