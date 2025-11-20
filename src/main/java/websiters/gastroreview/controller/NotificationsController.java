package websiters.gastroreview.controller;

import websiters.gastroreview.dto.NotificationRequest;
import websiters.gastroreview.dto.NotificationResponse;
import websiters.gastroreview.service.NotificationService;

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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Notifications", description = "User notifications for events, comments, ratings, achievements, etc.")
public class NotificationsController {

    private final NotificationService service;

    @GetMapping
    @Operation(summary = "List notifications", description = "Paginated list of all notifications (5 per page).")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class))))
    public Page<NotificationResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List notifications by user")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved")
    public Page<NotificationResponse> byUser(
            @PathVariable UUID userId,
            Pageable pageable) {

        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
            return service.findByUser(userId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public NotificationResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create notification")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid notification data")
    })
    public NotificationResponse create(
            @Valid @RequestBody NotificationRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public NotificationResponse markRead(@PathVariable UUID id) {
        return service.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete notification")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
