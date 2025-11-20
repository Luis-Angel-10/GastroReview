package websiters.gastroreview.controller;

import websiters.gastroreview.dto.AlertRequest;
import websiters.gastroreview.dto.AlertResponse;
import websiters.gastroreview.service.AlertService;

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
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Alerts", description = "System-generated alerts for suspicious, trending or moderated content.")
public class AlertsController {

    private final AlertService service;

    @GetMapping
    @Operation(summary = "List alerts", description = "Paginated list of all alerts (5 per page).")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AlertResponse.class))))
    public Page<AlertResponse> list(
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "List alerts by restaurant")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved")
    public Page<AlertResponse> byRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/review/{reviewId}")
    @Operation(summary = "List alerts by review")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved")
    public Page<AlertResponse> byReview(
            @PathVariable UUID reviewId,
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByReview(reviewId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get alert by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlertResponse.class))),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public AlertResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create alert")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alert created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlertResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid alert data")
    })
    public AlertResponse create(@Valid @RequestBody AlertRequest in) {
        return service.create(in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete alert")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alert deleted"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
