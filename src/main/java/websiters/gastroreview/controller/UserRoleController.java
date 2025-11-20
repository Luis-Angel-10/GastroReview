package websiters.gastroreview.controller;

import websiters.gastroreview.dto.UserRoleRequest;
import websiters.gastroreview.dto.UserRoleResponse;
import websiters.gastroreview.service.UserRoleService;

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
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Roles", description = "Operations for managing user-role assignments within the system.")
public class UserRoleController {

    private final UserRoleService service;

    @GetMapping
    @Operation(summary = "List user-role assignments", description = "Retrieves a paginated and optionally filtered list of user-role relationships.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserRoleResponse.class))))
    })
    public Page<UserRoleResponse> list(
            @Parameter(description = "Optional user ID filter") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Optional role ID filter") @RequestParam(required = false) UUID roleId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(userId, roleId, pageable);
    }

    @GetMapping("/{userId}/{roleId}")
    @Operation(summary = "Get user-role assignment", description = "Fetches details of a specific user-role relationship.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignment found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRoleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    public UserRoleResponse get(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Role ID (UUID)", required = true)
            @PathVariable UUID roleId) {
        return service.get(userId, roleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user-role assignment", description = "Assigns a role to a user. Fails if the assignment already exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assignment created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user or role data"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "409", description = "User already has this role")
    })
    public UserRoleResponse create(
            @Parameter(description = "User-role creation payload", required = true)
            @Valid @RequestBody UserRoleRequest in) {
        return service.create(in);
    }

    @PutMapping("/{userId}/{roleId}")
    @Operation(summary = "Update user-role assignment", description = "Updates a user-role record without changing its identifiers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignment updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or attempt to modify identifiers"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    public UserRoleResponse update(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Role ID (UUID)", required = true)
            @PathVariable UUID roleId,
            @Parameter(description = "Updated user-role payload", required = true)
            @Valid @RequestBody UserRoleRequest in) {
        return service.update(userId, roleId, in);
    }

    @DeleteMapping("/{userId}/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user-role assignment", description = "Removes a user-role relationship from the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Assignment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete: role assignment is referenced elsewhere")
    })
    public void delete(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Role ID (UUID)", required = true)
            @PathVariable UUID roleId) {
        service.delete(userId, roleId);
    }
}
