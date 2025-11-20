package websiters.gastroreview.controller;

import websiters.gastroreview.dto.CategoryRequest;
import websiters.gastroreview.dto.CategoryResponse;
import websiters.gastroreview.service.RestaurantCategoryService;

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

@RestController
@RequestMapping("/api/restaurant-categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurant Categories", description = "CRUD operations to manage restaurant categories and types.")
public class RestaurantCategoryController {

    private final RestaurantCategoryService service;

    @Operation(
            summary = "Create restaurant category",
            description = "Creates a new restaurant category with the provided details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category data"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @Parameter(description = "Category data", required = true)
            @RequestBody @Validated CategoryRequest req) {
        return service.create(req);
    }

    @Operation(
            summary = "Get restaurant category",
            description = "Retrieves a restaurant category by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public CategoryResponse get(
            @Parameter(description = "Category ID", required = true)
            @PathVariable("id") Integer id) {
        return service.get(id);
    }

    @Operation(
            summary = "List restaurant categories",
            description = "Retrieves a paginated list (5 per page) of all restaurant categories."
    )
    @ApiResponse(responseCode = "200", description = "List retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))))
    @GetMapping
    public Page<CategoryResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixedPageable);
    }

    @Operation(
            summary = "Update restaurant category",
            description = "Updates an existing restaurant category with the provided information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PutMapping("/{id}")
    public CategoryResponse update(
            @Parameter(description = "Category ID", required = true)
            @PathVariable("id") Integer id,
            @Parameter(description = "Updated category data", required = true)
            @RequestBody @Validated CategoryRequest req) {
        return service.update(id, req);
    }

    @Operation(
            summary = "Delete restaurant category",
            description = "Deletes a restaurant category by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Category ID", required = true)
            @PathVariable("id") Integer id) {
        service.delete(id);
    }
}
