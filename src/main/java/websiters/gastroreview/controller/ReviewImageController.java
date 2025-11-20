package websiters.gastroreview.controller;

import websiters.gastroreview.dto.ReviewImageRequest;
import websiters.gastroreview.dto.ReviewImageResponse;
import websiters.gastroreview.service.ReviewImageService;

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
@RequestMapping("/api/review-images")
@RequiredArgsConstructor
@Validated
@Tag(name = "ReviewImages", description = "Images associated with reviews.")
public class ReviewImageController {

    private final ReviewImageService service;

    @GetMapping
    @Operation(summary = "List review images", description = "Paginated list of all review images (5 per page).")
    @ApiResponse(responseCode = "200", description = "Review images retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReviewImageResponse.class))))
    public Page<ReviewImageResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List images by review", description = "Images belonging to a specific review.")
    public Page<ReviewImageResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewImageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ReviewImageResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new review image")
    public ReviewImageResponse create(
            @Valid @RequestBody ReviewImageRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review image")
    public ReviewImageResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewImageRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete review image")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
