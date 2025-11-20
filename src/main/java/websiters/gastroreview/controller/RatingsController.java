package websiters.gastroreview.controller;

import websiters.gastroreview.dto.RatingRequest;
import websiters.gastroreview.dto.RatingResponse;
import websiters.gastroreview.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Ratings", description = "Users' star ratings on reviews.")
public class RatingsController {

    private final RatingService service;

    @GetMapping
    @Operation(summary = "List ratings", description = "Paginated list of all ratings (5 per page).")
    @ApiResponse(responseCode = "200", description = "Ratings retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RatingResponse.class))))
    public Page<RatingResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List ratings by review")
    public Page<RatingResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List ratings by user")
    public Page<RatingResponse> byUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByUser(userId, fixed);
    }

    @GetMapping("/review/{reviewId}/average")
    @Operation(summary = "Get average rating for a review")
    public Double avg(@PathVariable UUID reviewId) {
        return service.getAverageStars(reviewId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RatingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    public RatingResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create rating")
    public RatingResponse create(
            @Valid @RequestBody RatingRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rating")
    public RatingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody RatingRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete rating")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
