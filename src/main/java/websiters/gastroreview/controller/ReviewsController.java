package websiters.gastroreview.controller;

import websiters.gastroreview.dto.ReviewRequest;
import websiters.gastroreview.dto.ReviewResponse;
import websiters.gastroreview.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reviews", description = "Operations related to restaurant reviews.")
public class ReviewsController {

    private final ReviewService service;

    @GetMapping
    @Operation(
            summary = "List reviews",
            description = "Retrieves a paginated list (5 per page) of all reviews."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Review list retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class))
            )
    )
    public Page<ReviewResponse> list(@Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(
            summary = "List reviews of a restaurant",
            description = "Retrieves a paginated list (5 per page) of reviews for a given restaurant."
    )
    public Page<ReviewResponse> byRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "List reviews by user",
            description = "Retrieves a paginated list (5 per page) of reviews created by a user."
    )
    public Page<ReviewResponse> byUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByUser(userId, fixed);
    }

    @GetMapping("/dish/{dishId}")
    @Operation(
            summary = "List reviews by dish",
            description = "Retrieves a paginated list (5 per page) of reviews associated with a dish."
    )
    public Page<ReviewResponse> byDish(
            @PathVariable UUID dishId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByDish(dishId, fixed);
    }

    @GetMapping("/with-images")
    @Operation(
            summary = "List reviews with images",
            description = "Retrieves reviews that have images (hasImage = true)."
    )
    public Page<ReviewResponse> withImages(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findWithImages(fixed);
    }

    @GetMapping("/with-audios")
    @Operation(
            summary = "List reviews with audios",
            description = "Retrieves reviews that have audio (hasAudio = true)."
    )
    public Page<ReviewResponse> withAudios(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findWithAudios(fixed);
    }

    @GetMapping("/search/{text}")
    @Operation(
            summary = "Search reviews by text",
            description = "Search across review title and content (contains), paginated (5 per page)."
    )
    public Page<ReviewResponse> searchByText(
            @PathVariable String text,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.searchByText(text, fixed);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get review by ID",
            description = "Retrieves detailed information of a review by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ReviewResponse get(
            @Parameter(description = "Review UUID", required = true)
            @PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new review",
            description = "Creates a new review for a restaurant (optionally associated with a dish)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review data"),
            @ApiResponse(responseCode = "404", description = "User, restaurant or dish not found")
    })
    public ReviewResponse create(
            @Parameter(description = "Review creation request body", required = true)
            @Valid @RequestBody ReviewRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update review",
            description = "Updates the content, media flags or dish of an existing review."
    )
    public ReviewResponse update(
            @Parameter(description = "Review UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete review",
            description = "Removes a review by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public void delete(
            @Parameter(description = "Review UUID", required = true)
            @PathVariable UUID id) {
        service.delete(id);
    }
}
