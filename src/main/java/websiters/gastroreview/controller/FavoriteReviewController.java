package websiters.gastroreview.controller;

import websiters.gastroreview.dto.FavoriteReviewRequest;
import websiters.gastroreview.dto.FavoriteReviewResponse;
import websiters.gastroreview.service.FavoriteReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/favorite-reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "Favorite Reviews", description = "Users marking reviews as favorite.")
public class FavoriteReviewController {

    private final FavoriteReviewService service;

    @GetMapping
    @Operation(summary = "List favorites")
    public Page<FavoriteReviewResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List favorites by user")
    public Page<FavoriteReviewResponse> byUser(
            @PathVariable UUID userId,
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByUser(userId, fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List favorites by review")
    public Page<FavoriteReviewResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/{userId}/{reviewId}")
    @Operation(summary = "Get favorite by user + review ID")
    public FavoriteReviewResponse get(
            @PathVariable UUID userId,
            @PathVariable UUID reviewId) {
        return service.get(userId, reviewId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create favorite")
    @ApiResponse(responseCode = "201", description = "Favorite created")
    public FavoriteReviewResponse create(
            @Valid @RequestBody FavoriteReviewRequest in) {
        return service.create(in);
    }

    @DeleteMapping("/{userId}/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete favorite")
    public void delete(
            @PathVariable UUID userId,
            @PathVariable UUID reviewId) {
        service.delete(userId, reviewId);
    }
}
