package websiters.gastroreview.controller;

import jakarta.validation.Valid;
import websiters.gastroreview.dto.RestaurantRequest;
import websiters.gastroreview.dto.RestaurantResponse;
import websiters.gastroreview.service.RestaurantService;

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
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurants", description = "CRUD operations for managing restaurants and their main information.")
public class RestaurantsController {

    private final RestaurantService service;

    @GetMapping
    @Operation(summary = "List restaurants", description = "Retrieves a paginated list (5 per page) of all registered restaurants.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RestaurantResponse.class))))
    public Page<RestaurantResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixedPageable);
    }

    @GetMapping("/search/name/{name}")
    @Operation(summary = "Search restaurants by name", description = "Finds restaurants whose names contain the given text (paginated, 5 per page).")
    public Page<RestaurantResponse> findByName(
            @PathVariable("name") String name,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByName(name, fixedPageable);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "List restaurants by owner", description = "Retrieves all restaurants belonging to a specific owner (paginated, 5 per page).")
    public Page<RestaurantResponse> findByOwner(
            @PathVariable("ownerId") UUID ownerId,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByOwner(ownerId, fixedPageable);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "List restaurants by city", description = "Retrieves all restaurants located in a specific city (paginated, 5 per page).")
    public Page<RestaurantResponse> findByCity(
            @PathVariable("city") String city,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByCity(city, fixedPageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves detailed information of a restaurant by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public RestaurantResponse get(
            @Parameter(description = "Restaurant UUID", required = true)
            @PathVariable("id") UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create restaurant", description = "Registers a new restaurant in the system.")
    public RestaurantResponse create(
            @Parameter(description = "Restaurant data", required = true)
            @Valid @RequestBody RestaurantRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Updates the information of an existing restaurant.")
    public RestaurantResponse update(
            @Parameter(description = "Restaurant UUID", required = true)
            @PathVariable("id") UUID id,
            @Valid @RequestBody RestaurantRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete restaurant", description = "Deletes a restaurant by its unique ID.")
    public void delete(
            @Parameter(description = "Restaurant UUID", required = true)
            @PathVariable("id") UUID id) {
        service.delete(id);
    }
}
