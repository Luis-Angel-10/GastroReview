package websiters.gastroreview.controller;

import websiters.gastroreview.dto.RestaurantAddressRequest;
import websiters.gastroreview.dto.RestaurantAddressResponse;
import websiters.gastroreview.service.RestaurantAddressService;
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
@RequestMapping("/api/restaurant-addresses")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurant Addresses", description = "Operations to manage restaurant–address associations (branches and locations).")
public class RestaurantAddressController {

    private final RestaurantAddressService service;

    @GetMapping
    @Operation(
            summary = "List restaurant-address associations",
            description = "Retrieves a paginated list (5 per page) of all associations between restaurants and addresses."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RestaurantAddressResponse.class))
            )
    )
    public Page<RestaurantAddressResponse> list(@Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixedPageable);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(
            summary = "List addresses of a restaurant",
            description = "Retrieves a paginated list (5 per page) of addresses linked to a given restaurant."
    )
    public Page<RestaurantAddressResponse> getByRestaurant(
            @Parameter(description = "Restaurant UUID", required = true)
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixedPageable);
    }

    @GetMapping("/address/{addressId}")
    @Operation(
            summary = "List restaurants linked to an address",
            description = "Retrieves a paginated list (5 per page) of restaurants linked to a given address."
    )
    public Page<RestaurantAddressResponse> getByAddress(
            @Parameter(description = "Address UUID", required = true)
            @PathVariable UUID addressId,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByAddress(addressId, fixedPageable);
    }

    @GetMapping("/{restaurantId}/{addressId}")
    @Operation(
            summary = "Get a restaurant-address association",
            description = "Retrieves a specific association between a restaurant and an address by their IDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Association found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantAddressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Association not found")
    })
    public RestaurantAddressResponse get(
            @Parameter(description = "Restaurant UUID", required = true)
            @PathVariable UUID restaurantId,
            @Parameter(description = "Address UUID", required = true)
            @PathVariable UUID addressId) {
        return service.get(restaurantId, addressId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create restaurant-address association",
            description = "Creates a new link between a restaurant and an address. Marks it as primary if indicated."
    )
    public RestaurantAddressResponse create(
            @Parameter(description = "Restaurant-address association request", required = true)
            @RequestBody @Validated RestaurantAddressRequest in) {
        return service.create(in);
    }

    @PutMapping("/{restaurantId}/{addressId}")
    @Operation(
            summary = "Update restaurant-address association",
            description = "Updates an existing association, including primary branch status or branch name."
    )
    public RestaurantAddressResponse update(
            @PathVariable UUID restaurantId,
            @PathVariable UUID addressId,
            @RequestBody @Validated RestaurantAddressRequest in) {
        return service.update(restaurantId, addressId, in);
    }

    @DeleteMapping("/{restaurantId}/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete restaurant-address association",
            description = "Deletes the association between a restaurant and an address."
    )
    public void delete(
            @PathVariable UUID restaurantId,
            @PathVariable UUID addressId) {
        service.delete(restaurantId, addressId);
    }
}
