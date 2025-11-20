package websiters.gastroreview.controller;

import websiters.gastroreview.dto.UserRequest;
import websiters.gastroreview.dto.UserResponse;
import websiters.gastroreview.service.UserService;

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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User", description = "CRUD operations, search, and authentication endpoints for user management.")
public class UserController {

    private final UserService service;

    @GetMapping
    @Operation(summary = "List users (paginated and filtered)",
            description = "Retrieves a paginated and optionally filtered list of users.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public Page<UserResponse> list(
            @Parameter(description = "Optional email filter") @RequestParam(required = false) String email,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return service.list(email, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Fetches a user by their unique UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse get(
            @Parameter(description = "User UUID", required = true)
            @PathVariable("id") UUID id) {
        return service.get(id);
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Registers a new user in the system (admin operation).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponse> create(
            @Parameter(description = "User data payload", required = true)
            @Valid @RequestBody UserRequest in) {
        UserResponse created = service.create(in);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates the email or password of an existing user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public UserResponse update(
            @Parameter(description = "User UUID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Updated user payload", required = true)
            @Valid @RequestBody UserRequest in) {
        return service.update(id, in);
    }

    @GetMapping("/search/{email}")
    @Operation(summary = "Get user by email", description = "Fetches a user record by their registered email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse getByEmail(
            @Parameter(description = "Email address of the user", required = true)
            @PathVariable String email) {
        return service.getByEmail(email);
    }

    @PostMapping("/signup")
    @Operation(summary = "User registration", description = "Registers a new user account (USER or OWNER).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponse> signup(
            @Parameter(description = "Registration payload", required = true)
            @Valid @RequestBody UserRequest request) {
        UserResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Validates credentials and returns user data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> signin(
            @Parameter(description = "User email", required = true) @RequestParam String email,
            @Parameter(description = "Password", required = true) @RequestParam String password) {
        UserResponse user = service.authenticate(email, password);
        return ResponseEntity.ok(user);
    }
}
