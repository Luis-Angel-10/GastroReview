package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.ReviewImageRequest;
import websiters.gastroreview.dto.ReviewImageResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Review;
import websiters.gastroreview.model.ReviewImage;
import websiters.gastroreview.repository.ReviewImageRepository;
import websiters.gastroreview.repository.ReviewRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewImageService {

    private final ReviewImageRepository repo;
    private final ReviewRepository reviewRepo;

    @Transactional(readOnly = true)
    public Page<ReviewImageResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewImageResponse> findByReview(UUID reviewId, Pageable pageable) {
        List<ReviewImage> list = repo.findByReview_Id(reviewId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public ReviewImageResponse get(UUID id) {
        ReviewImage img = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review image not found"));
        return Mappers.toResponse(img);
    }

    @Transactional
    public ReviewImageResponse create(ReviewImageRequest in) {

        Review review = reviewRepo.findById(in.getReviewId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not exist"));

        ReviewImage img = Mappers.toReviewImageEntity(in, review);
        repo.save(img);

        return Mappers.toResponse(img);
    }

    @Transactional
    public ReviewImageResponse update(UUID id, ReviewImageRequest in) {

        ReviewImage img = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review image not found"));

        if (in.getUrl() != null) img.setUrl(in.getUrl());
        if (in.getAltText() != null) img.setAltText(in.getAltText());

        repo.save(img);
        return Mappers.toResponse(img);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review image not found");
        }
        repo.deleteById(id);
    }

    private Page<ReviewImageResponse> paginate(List<ReviewImage> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<ReviewImageResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
