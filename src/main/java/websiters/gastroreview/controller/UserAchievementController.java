package websiters.gastroreview.controller;

import jakarta.validation.Valid;
import websiters.gastroreview.dto.UserAchievementRequest;
import websiters.gastroreview.dto.UserAchievementResponse;
import websiters.gastroreview.service.UserAchievementService;

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

import java.util.UUID;

@RestController
@RequestMapping("/api/user-achievements")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Achievements", description = "CRUD operations for managing user achievements and badges.")
public class UserAchievementController {

    private final UserAchievementService service;

    @GetMapping
    @Operation(summary = "List user achievements", description = "Retrieves a paginated and optionally filtered list of user achievements.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserAchievementResponse.class))))
    })
    public Page<UserAchievementResponse> list(
            @Parameter(description = "Optional badge filter") @RequestParam(required = false) String badge,
            @Parameter(description = "Optional user ID filter") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(userId, badge, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get achievement by ID", description = "Retrieves detailed information of a specific user achievement by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Achievement found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAchievementResponse.class))),
            @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public UserAchievementResponse get(
            @Parameter(description = "Achievement ID", required = true)
            @PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create achievement", description = "Registers a new user achievement (badge, level, stars).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Achievement created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAchievementResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate badge for the user")
    })
    public UserAchievementResponse create(
            @Parameter(description = "Achievement payload", required = true)
            @Valid @RequestBody UserAchievementRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update achievement", description = "Updates the details of an existing user achievement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Achievement updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAchievementResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Achievement not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate badge for the user")
    })
    public UserAchievementResponse update(
            @Parameter(description = "Achievement ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Updated achievement payload", required = true)
            @Valid @RequestBody UserAchievementRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete achievement", description = "Deletes a user achievement permanently by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Achievement deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public void delete(
            @Parameter(description = "Achievement ID", required = true)
            @PathVariable("id") Long id) {
        service.delete(id);
    }
}
