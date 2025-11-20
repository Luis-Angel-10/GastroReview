package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.RatingRequest;
import websiters.gastroreview.dto.RatingResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Rating;
import websiters.gastroreview.model.Review;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.RatingRepository;
import websiters.gastroreview.repository.ReviewRepository;
import websiters.gastroreview.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository repo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public Page<RatingResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> findByReview(UUID reviewId, Pageable pageable) {
        List<Rating> list = repo.findByReview_Id(reviewId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> findByUser(UUID userId, Pageable pageable) {
        List<Rating> list = repo.findByUser_Id(userId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Double getAverageStars(UUID reviewId) {
        return repo.getAverageStarsByReview(reviewId);
    }

    @Transactional(readOnly = true)
    public RatingResponse get(UUID id) {
        Rating r = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));
        return Mappers.toResponse(r);
    }

    @Transactional
    public RatingResponse create(RatingRequest in) {

        Review review = reviewRepo.findById(in.getReviewId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not exist"));

        User user = userRepo.findById(in.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));

        Rating rating = Mappers.toRatingEntity(in, user, review);
        repo.save(rating);

        return Mappers.toResponse(rating);
    }

    @Transactional
    public RatingResponse update(UUID id, RatingRequest in) {
        Rating rating = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));

        if (in.getStars() != null) rating.setStars(in.getStars());
        if (in.getPoints() != null) rating.setPoints(in.getPoints());

        repo.save(rating);
        return Mappers.toResponse(rating);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");
        }
        repo.deleteById(id);
    }

    private Page<RatingResponse> paginate(List<Rating> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = size * page;
        int end = Math.min(start + size, list.size());

        List<RatingResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
