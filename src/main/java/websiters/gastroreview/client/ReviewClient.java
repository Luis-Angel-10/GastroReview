package websiters.gastroreview.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import websiters.gastroreview.dto.ReviewResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "review", path = "/api/reviews")
public interface ReviewClient {

    @GetMapping("/restaurant/{restaurantId}")
    List<ReviewResponse> getReviewsByRestaurant(@PathVariable UUID restaurantId);

    @GetMapping("/user/{userId}")
    List<ReviewResponse> getReviewsByUser(@PathVariable UUID userId);

    @GetMapping("/{id}")
    ReviewResponse getReview(@PathVariable UUID id);
}
