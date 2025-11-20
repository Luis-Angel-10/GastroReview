package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.ReviewCommentRequest;
import websiters.gastroreview.dto.ReviewCommentResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.*;
import websiters.gastroreview.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewCommentRepository repo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public Page<ReviewCommentResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewCommentResponse> findByReview(UUID reviewId, Pageable pageable) {
        List<ReviewComment> list = repo.findByReview_Id(reviewId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewCommentResponse> findByAuthor(UUID userId, Pageable pageable) {
        List<ReviewComment> list = repo.findByAuthor_Id(userId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewCommentResponse> findReplies(UUID parentId, Pageable pageable) {
        List<ReviewComment> list = repo.findByParent_Id(parentId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public ReviewCommentResponse get(UUID id) {
        ReviewComment comment = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        return Mappers.toResponse(comment);
    }

    @Transactional
    public ReviewCommentResponse create(ReviewCommentRequest in) {

        Review review = reviewRepo.findById(in.getReviewId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not exist"));

        User author = userRepo.findById(in.getAuthorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));

        ReviewComment parent = null;

        if (in.getParentId() != null) {
            parent = repo.findById(in.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment does not exist"));
        }

        ReviewComment comment = Mappers.toReviewCommentEntity(in, author, review, parent);
        repo.save(comment);

        return Mappers.toResponse(comment);
    }

    @Transactional
    public ReviewCommentResponse update(UUID id, ReviewCommentRequest in) {

        ReviewComment comment = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (in.getContent() != null) comment.setContent(in.getContent());

        repo.save(comment);
        return Mappers.toResponse(comment);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        repo.deleteById(id);
    }

    private Page<ReviewCommentResponse> paginate(List<ReviewComment> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<ReviewCommentResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
