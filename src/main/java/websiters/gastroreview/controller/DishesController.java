package websiters.gastroreview.controller;

import websiters.gastroreview.dto.DishRequest;
import websiters.gastroreview.dto.DishResponse;
import websiters.gastroreview.service.DishService;

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
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dishes", description = "CRUD operations and search filters for restaurant dishes.")
public class DishesController {

    private final DishService service;

    @GetMapping
    @Operation(
            summary = "List dishes",
            description = "Retrieves a paginated list (5 per page) of all dishes.")
    @ApiResponse(
            responseCode = "200",
            description = "List of dishes retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DishResponse.class))
            )
    )
    public Page<DishResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/search/{name}")
    @Operation(
            summary = "Search dishes by name",
            description = "Finds dishes whose names contain the given text (paginated 5 per page).")
    public Page<DishResponse> searchByName(
            @PathVariable String name,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.searchByName(name, fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(
            summary = "List dishes by restaurant",
            description = "Retrieves all dishes belonging to a specific restaurant (paginated 5 per page).")
    public Page<DishResponse> findByRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get dish by ID",
            description = "Retrieves detailed information of a dish by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dish found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DishResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public DishResponse get(
            @Parameter(description = "Dish UUID", required = true)
            @PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create new dish",
            description = "Registers a new dish in the menu of a restaurant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Dish created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DishResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid dish data"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public DishResponse create(
            @Parameter(description = "Dish creation request body", required = true)
            @Validated @RequestBody DishRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update dish",
            description = "Updates the information of an existing dish in a restaurant."
    )
    public DishResponse update(
            @Parameter(description = "Dish UUID", required = true)
            @PathVariable UUID id,
            @Validated @RequestBody DishRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a dish",
            description = "Removes a dish by its unique ID from the restaurant menu."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dish deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public void delete(
            @Parameter(description = "Dish UUID", required = true)
            @PathVariable UUID id) {
        service.delete(id);
    }
}
