package websiters.gastroreview.controller;

import websiters.gastroreview.dto.ReviewAudioRequest;
import websiters.gastroreview.dto.ReviewAudioResponse;
import websiters.gastroreview.service.ReviewAudioService;

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
@RequestMapping("/api/review-audios")
@RequiredArgsConstructor
@Validated
@Tag(name = "ReviewAudios", description = "Audios associated with reviews.")
public class ReviewAudioController {

    private final ReviewAudioService service;

    @GetMapping
    @Operation(summary = "List review audios", description = "Paginated list of review audios (5 per page).")
    @ApiResponse(responseCode = "200", description = "Audios retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReviewAudioResponse.class))))
    public Page<ReviewAudioResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List audios by review", description = "Retrieves all audios belonging to a review.")
    public Page<ReviewAudioResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audio by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audio found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewAudioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Audio not found")
    })
    public ReviewAudioResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create review audio")
    public ReviewAudioResponse create(
            @Valid @RequestBody ReviewAudioRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review audio")
    public ReviewAudioResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewAudioRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete review audio")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
