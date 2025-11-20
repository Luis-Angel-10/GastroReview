package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.ReviewRequest;
import websiters.gastroreview.dto.ReviewResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Dish;
import websiters.gastroreview.model.Restaurant;
import websiters.gastroreview.model.Review;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.DishRepository;
import websiters.gastroreview.repository.RestaurantRepository;
import websiters.gastroreview.repository.ReviewRepository;
import websiters.gastroreview.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repo;
    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;
    private final DishRepository dishRepo;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<Review> all = repo.findByRestaurant_Id(restaurantId);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findByUser(UUID userId, Pageable pageable) {
        List<Review> all = repo.findByAuthor_Id(userId);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findByDish(UUID dishId, Pageable pageable) {
        List<Review> all = repo.findByDish_Id(dishId);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findWithImages(Pageable pageable) {
        List<Review> all = repo.findByHasImageTrue();
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findWithAudios(Pageable pageable) {
        List<Review> all = repo.findByHasAudioTrue();
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchByText(String text, Pageable pageable) {
        List<Review> all = repo.searchByText(text);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public ReviewResponse get(UUID id) {
        Review r = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        return Mappers.toResponse(r);
    }

    @Transactional
    public ReviewResponse create(ReviewRequest in) {

        User user = userRepo.findById(in.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));

        Restaurant restaurant = restaurantRepo.findById(in.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant does not exist"));

        Dish dish = null;
        if (in.getDishId() != null) {
            dish = dishRepo.findById(in.getDishId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dish does not exist"));
        }

        Review review = Mappers.toReviewEntity(in, user, restaurant, dish);

        repo.save(review);
        return Mappers.toResponse(review);
    }

    @Transactional
    public ReviewResponse update(UUID id, ReviewRequest in) {
        Review review = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        if (in.getTitle() != null) review.setTitle(in.getTitle());
        if (in.getContent() != null) review.setContent(in.getContent());
        if (in.getHasAudio() != null) review.setHasAudio(in.getHasAudio());
        if (in.getHasImage() != null) review.setHasImage(in.getHasImage());

        if (in.getDishId() != null) {
            Dish dish = dishRepo.findById(in.getDishId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dish does not exist"));
            review.setDish(dish);
        }

        repo.save(review);
        return Mappers.toResponse(review);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }
        repo.deleteById(id);
    }

    private Page<ReviewResponse> paginate(List<Review> all, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, all.size());

        List<ReviewResponse> content = all.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), all.size());
    }
}
