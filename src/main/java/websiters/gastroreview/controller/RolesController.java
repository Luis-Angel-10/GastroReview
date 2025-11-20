package websiters.gastroreview.controller;

import jakarta.validation.Valid;
import websiters.gastroreview.dto.RoleRequest;
import websiters.gastroreview.dto.RoleResponse;
import websiters.gastroreview.service.RoleService;

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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Roles", description = "CRUD operations for managing user roles within the system.")
public class RolesController {

    private final RoleService service;

    @GetMapping
    @Operation(summary = "List roles", description = "Retrieves a paginated and optionally filtered list of system roles.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RoleResponse.class))))
    public Page<RoleResponse> list(
            @Parameter(description = "Optional name filter") @RequestParam(required = false) String name,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(name, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Fetches details of a specific role by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public RoleResponse get(
            @Parameter(description = "Role ID", required = true)
            @PathVariable("id") UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create role", description = "Creates a new role in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid role data"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public RoleResponse create(
            @Parameter(description = "Role data", required = true)
            @Valid @RequestBody RoleRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Updates an existing role with new information.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public RoleResponse update(
            @Parameter(description = "Role ID", required = true)
            @PathVariable("id") UUID id,
            @Valid @RequestBody RoleRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete role", description = "Deletes a role from the system by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public void delete(
            @Parameter(description = "Role ID", required = true)
            @PathVariable("id") UUID id) {
        service.delete(id);
    }
}
