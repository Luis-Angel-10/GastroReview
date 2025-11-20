package websiters.gastroreview.controller;

import websiters.gastroreview.dto.FriendshipRequest;
import websiters.gastroreview.dto.FriendshipResponse;
import websiters.gastroreview.service.FriendshipService;
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
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@Validated
@Tag(name = "Friendships", description = "Operations related to user following relationships (followers and following).")
public class FriendshipController {

    private final FriendshipService service;

    @GetMapping
    @Operation(
            summary = "List friendships",
            description = "Retrieves a paginated list of all user follow relationships (5 per page)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Friendship list retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class))
            )
    )
    public Page<FriendshipResponse> list(@Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixedPageable);
    }

    @GetMapping("/followers/{userId}")
    @Operation(
            summary = "List followers of a user",
            description = "Retrieves a paginated list (5 per page) of users who follow a given user."
    )
    public Page<FriendshipResponse> getFollowers(@PathVariable UUID userId, Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findFollowers(userId, fixedPageable);
    }

    @GetMapping("/following/{userId}")
    @Operation(
            summary = "List users followed by a user",
            description = "Retrieves a paginated list (5 per page) of users followed by a given user."
    )
    public Page<FriendshipResponse> getFollowing(@PathVariable UUID userId, Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findFollowing(userId, fixedPageable);
    }

    @GetMapping("/{followerId}/{followedId}")
    @Operation(
            summary = "Get friendship by IDs",
            description = "Retrieves a specific following relationship by follower and followed UUIDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friendship found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FriendshipResponse.class))),
            @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    public FriendshipResponse get(
            @Parameter(description = "Follower user UUID", required = true)
            @PathVariable UUID followerId,
            @Parameter(description = "Followed user UUID", required = true)
            @PathVariable UUID followedId) {
        return service.get(followerId, followedId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new friendship",
            description = "Creates a new following relationship between users (follower → followed)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Friendship created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FriendshipResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid friendship data"),
            @ApiResponse(responseCode = "409", description = "Friendship already exists")
    })
    public FriendshipResponse create(
            @Parameter(description = "Friendship creation request body", required = true)
            @RequestBody @Validated FriendshipRequest in) {
        return service.create(in);
    }

    @DeleteMapping("/{followerId}/{followedId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete friendship",
            description = "Removes a following relationship between two users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Friendship deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    public void delete(
            @Parameter(description = "Follower user UUID", required = true)
            @PathVariable UUID followerId,
            @Parameter(description = "Followed user UUID", required = true)
            @PathVariable UUID followedId) {
        service.delete(followerId, followedId);
    }
}
