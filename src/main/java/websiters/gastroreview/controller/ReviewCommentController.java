package websiters.gastroreview.controller;

import websiters.gastroreview.dto.ReviewCommentAnalysisResponse;
import websiters.gastroreview.dto.ReviewCommentRequest;
import websiters.gastroreview.dto.ReviewCommentResponse;
import websiters.gastroreview.service.ReviewCommentService;

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
@RequestMapping("/api/review-comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "ReviewComments", description = "Comments and threaded replies on reviews.")
public class ReviewCommentController {

    private final ReviewCommentService service;

    @GetMapping
    @Operation(summary = "List all comments", description = "Paginated list (5 per page).")
    @ApiResponse(responseCode = "200", description = "Comments retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReviewCommentResponse.class))))
    public Page<ReviewCommentResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List comments for a review")
    public Page<ReviewCommentResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/author/{userId}")
    @Operation(summary = "List comments written by a user")
    public Page<ReviewCommentResponse> byAuthor(
            @PathVariable UUID userId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByAuthor(userId, fixed);
    }

    @GetMapping("/replies/{parentId}")
    @Operation(summary = "List replies of a comment")
    public Page<ReviewCommentResponse> replies(
            @PathVariable UUID parentId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findReplies(parentId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewCommentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ReviewCommentResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create comment or reply")
    public ReviewCommentResponse create(
            @Valid @RequestBody ReviewCommentRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment content")
    public ReviewCommentResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewCommentRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete comment")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/{id}/analysis")
    @Operation(
            summary = "Get cognitive analysis of a comment",
            description = "Retrieves sentiment, key phrases, and confidence scores for the given comment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analysis retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewCommentAnalysisResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment or analysis not found")
    })
    public ReviewCommentAnalysisResponse getAnalysis(@PathVariable UUID id) {
        return service.getAnalysis(id);
    }

}
