package websiters.gastroreview.controller;

import websiters.gastroreview.dto.FavoriteRestaurantRequest;
import websiters.gastroreview.dto.FavoriteRestaurantResponse;
import websiters.gastroreview.service.FavoriteRestaurantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorite-restaurants")
@RequiredArgsConstructor
@Validated
@Tag(name = "FavoriteRestaurants", description = "Management of restaurant favorites by users.")
public class FavoriteRestaurantController {

    private final FavoriteRestaurantService service;

    @GetMapping
    @Operation(summary = "List all favorite marks", description = "Paginated list (5 per page).")
    @ApiResponse(responseCode = "200", description = "Favorites retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FavoriteRestaurantResponse.class))))
    public Page<FavoriteRestaurantResponse> list(@Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List favorites by user")
    public Page<FavoriteRestaurantResponse> byUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByUser(userId, fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "List users who favorited a restaurant")
    public Page<FavoriteRestaurantResponse> byRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/{userId}/{restaurantId}")
    @Operation(summary = "Get favorite record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorite found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteRestaurantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorite not found")
    })
    public FavoriteRestaurantResponse get(
            @PathVariable UUID userId,
            @PathVariable UUID restaurantId) {
        return service.get(userId, restaurantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Mark restaurant as favorite")
    public FavoriteRestaurantResponse create(@Valid @RequestBody FavoriteRestaurantRequest in) {
        return service.create(in);
    }

    @DeleteMapping("/{userId}/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove favorite mark")
    public void delete(@PathVariable UUID userId, @PathVariable UUID restaurantId) {
        service.delete(userId, restaurantId);
    }
}
