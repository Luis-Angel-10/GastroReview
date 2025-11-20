package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.FavoriteReviewRequest;
import websiters.gastroreview.dto.FavoriteReviewResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.*;
import websiters.gastroreview.repository.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteReviewService {

    private final FavoriteReviewRepository repo;
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> findByUser(UUID userId, Pageable pageable) {
        return repo.findByIdUserId(userId, pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> findByReview(UUID reviewId, Pageable pageable) {
        return repo.findByIdReviewId(reviewId, pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public FavoriteReviewResponse get(UUID userId, UUID reviewId) {
        FavoriteReviewId id = new FavoriteReviewId(userId, reviewId);

        FavoriteReview fr = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite review not found"));

        return Mappers.toResponse(fr);
    }

    @Transactional
    public FavoriteReviewResponse create(FavoriteReviewRequest in) {

        FavoriteReviewId id = new FavoriteReviewId(in.getUserId(), in.getReviewId());

        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already favorited");
        }

        User user = userRepo.findById(in.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Review review = reviewRepo.findById(in.getReviewId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        FavoriteReview fr = FavoriteReview.builder()
                .id(id)
                .user(user)
                .review(review)
                .build();

        fr = repo.save(fr);
        return Mappers.toResponse(fr);
    }

    @Transactional
    public void delete(UUID userId, UUID reviewId) {
        FavoriteReviewId id = new FavoriteReviewId(userId, reviewId);

        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }

        repo.deleteById(id);
    }
}
