package websiters.gastroreview.controller;

import websiters.gastroreview.dto.UserProfileRequest;
import websiters.gastroreview.dto.UserProfileResponse;
import websiters.gastroreview.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Profiles", description = "CRUD operations for managing user profiles and personal information.")
public class UserProfilesController {

    private final UserProfileService service;

    @GetMapping
    @Operation(summary = "List user profiles", description = "Retrieves a paginated and optionally filtered list of user profiles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserProfileResponse.class))))
    })
    public Page<UserProfileResponse> list(
            @Parameter(description = "Optional name filter") @RequestParam(required = false) String name,
            @Parameter(description = "Optional bio keyword filter") @RequestParam(required = false) String bio,
            @Parameter(description = "Optional active status filter") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(name, bio, active, pageable);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by user ID", description = "Retrieves a user profile by its associated user UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public UserProfileResponse get(
            @Parameter(description = "User UUID", required = true)
            @PathVariable("userId") UUID userId) {
        return service.get(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create or update profile", description = "Creates a new profile or updates an existing one if it already exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created or updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict – duplicate profile or constraint violation")
    })
    public UserProfileResponse upsert(
            @Parameter(description = "Profile creation payload", required = true)
            @Valid @RequestBody UserProfileRequest in) {
        return service.upsert(in);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update profile", description = "Updates an existing user profile with the provided information.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public UserProfileResponse update(
            @Parameter(description = "User UUID", required = true)
            @PathVariable("userId") UUID userId,
            @Parameter(description = "Updated profile payload", required = true)
            @Valid @RequestBody UserProfileRequest in) {
        return service.update(userId, in);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete profile", description = "Deletes a user profile permanently by its associated user UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public void delete(
            @Parameter(description = "User UUID", required = true)
            @PathVariable("userId") UUID userId) {
        service.delete(userId);
    }
}
