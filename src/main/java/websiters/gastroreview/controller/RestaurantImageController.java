package websiters.gastroreview.controller;

import websiters.gastroreview.dto.RestaurantImageRequest;
import websiters.gastroreview.dto.RestaurantImageResponse;
import websiters.gastroreview.service.RestaurantImageService;

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
@RequestMapping("/api/restaurant-images")
@RequiredArgsConstructor
@Validated
@Tag(name = "RestaurantImages", description = "Image management for restaurants.")
public class RestaurantImageController {

    private final RestaurantImageService service;

    @GetMapping
    @Operation(summary = "List restaurant images", description = "Paginated (5 per page) list of all restaurant images.")
    @ApiResponse(responseCode = "200", description = "Images retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RestaurantImageResponse.class))))
    public Page<RestaurantImageResponse> list(@Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "List images by restaurant", description = "Retrieves images belonging to a specific restaurant.")
    public Page<RestaurantImageResponse> findByRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/search/alt/{text}")
    @Operation(summary = "Search images by alt text", description = "Searches restaurant images by alt text (contains).")
    public Page<RestaurantImageResponse> findByAlt(
            @PathVariable String text,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.searchByAltText(text, fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by ID", description = "Retrieves image details by unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantImageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public RestaurantImageResponse get(@Parameter(description = "Image UUID") @PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload restaurant image", description = "Registers a new image for a restaurant.")
    public RestaurantImageResponse create(
            @Validated @RequestBody RestaurantImageRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update image", description = "Updates URL or alt text of an image.")
    public RestaurantImageResponse update(
            @PathVariable UUID id,
            @Validated @RequestBody RestaurantImageRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete image", description = "Removes a restaurant image.")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
