package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.client.ReviewClient;
import websiters.gastroreview.dto.FavoriteReviewRequest;
import websiters.gastroreview.dto.FavoriteReviewResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.FavoriteReview;
import websiters.gastroreview.model.FavoriteReviewId;
import websiters.gastroreview.repository.FavoriteReviewRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteReviewService {

    private final FavoriteReviewRepository repo;
    private final ReviewClient reviewClient;

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> findByUser(UUID userId, Pageable pageable) {
        List<FavoriteReview> list = repo.findById_UserId(userId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteReviewResponse> findByReview(UUID reviewId, Pageable pageable) {
        List<FavoriteReview> list = repo.findById_ReviewId(reviewId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public FavoriteReviewResponse get(UUID userId, UUID reviewId) {
        FavoriteReviewId id = new FavoriteReviewId(userId, reviewId);

        FavoriteReview fav = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        return Mappers.toResponse(fav);
    }

    @Transactional
    public FavoriteReviewResponse create(FavoriteReviewRequest in) {

        FavoriteReviewId id = new FavoriteReviewId(in.getUserId(), in.getReviewId());

        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already marked as favorite");
        }

        // Verifica que el review exista en el MS Review
        reviewClient.getReview(in.getReviewId());

        FavoriteReview fav = FavoriteReview.builder()
                .id(id)
                .userId(in.getUserId())
                .reviewId(in.getReviewId())
                .createdAt(OffsetDateTime.now())
                .build();

        repo.save(fav);

        return Mappers.toResponse(fav);
    }

    @Transactional
    public void delete(UUID userId, UUID reviewId) {
        FavoriteReviewId id = new FavoriteReviewId(userId, reviewId);

        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }

        repo.deleteById(id);
    }

    private Page<FavoriteReviewResponse> paginate(List<FavoriteReview> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<FavoriteReviewResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
