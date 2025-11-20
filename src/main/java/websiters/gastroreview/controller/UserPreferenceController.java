package websiters.gastroreview.controller;

import jakarta.validation.Valid;
import websiters.gastroreview.dto.UserPreferenceRequest;
import websiters.gastroreview.dto.UserPreferenceResponse;
import websiters.gastroreview.service.UserPreferenceService;

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
@RequestMapping("/api/user-preferences")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Preferences", description = "CRUD operations for managing user preferences and settings.")
public class UserPreferenceController {

    private final UserPreferenceService service;

    @GetMapping
    @Operation(summary = "List user preferences", description = "Retrieves a paginated and optionally filtered list of user preferences.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserPreferenceResponse.class))))
    })
    public Page<UserPreferenceResponse> list(
            @Parameter(description = "Optional user ID filter") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Optional preference key filter") @RequestParam(required = false) String prefKey,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(userId, prefKey, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get preference by ID", description = "Retrieves detailed information of a specific user preference by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPreferenceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    public UserPreferenceResponse get(
            @Parameter(description = "Preference ID", required = true)
            @PathVariable("id") UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create preference", description = "Creates a new user preference for a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Preference created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPreferenceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Preference key already exists for the user")
    })
    public UserPreferenceResponse create(
            @Parameter(description = "Preference creation payload", required = true)
            @Valid @RequestBody UserPreferenceRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update preference", description = "Updates an existing user preference for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPreferenceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Preference not found"),
            @ApiResponse(responseCode = "409", description = "Preference key already exists for the user")
    })
    public UserPreferenceResponse update(
            @Parameter(description = "Preference ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Updated preference payload", required = true)
            @Valid @RequestBody UserPreferenceRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete preference", description = "Deletes a user preference by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Preference deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    public void delete(
            @Parameter(description = "Preference ID", required = true)
            @PathVariable("id") UUID id) {
        service.delete(id);
    }
}
