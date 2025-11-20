package websiters.gastroreview.controller;

import websiters.gastroreview.dto.AddressRequest;
import websiters.gastroreview.dto.AddressResponse;
import websiters.gastroreview.service.AddressService;

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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "CRUD operations for Addresses")
public class  AddressController {

    private final AddressService service;


    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Retrieves a specific address by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public AddressResponse get(
            @Parameter(description = "UUID of the address", required = true)
            @PathVariable("id") UUID id) {
        return service.get(id);
    }

    @GetMapping
    @Operation(summary = "List addresses", description = "Retrieves a paginated list of all addresses.")
    @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class))))
    public Page<AddressResponse> list(Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixedPageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Updates an existing address by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public AddressResponse update(
            @Parameter(description = "UUID of the address to update", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Updated address data", required = true)
            @RequestBody AddressRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "List addresses by city", description = "Retrieves all addresses located in a specific city (paginated, 5 per page).")
    public Page<AddressResponse> getByCity(@PathVariable("city") String city, Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByCity(city, fixedPageable);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "List addresses by country", description = "Retrieves all addresses within a specific country (paginated, 5 per page).")
    public Page<AddressResponse> getByCountry(@PathVariable("country") String country, Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByCountry(country, fixedPageable);
    }

    @GetMapping("/street/{street}")
    @Operation(summary = "Search addresses by street name", description = "Retrieves addresses containing a given substring in the street name (paginated, 5 per page).")
    public Page<AddressResponse> getByStreet(@PathVariable("street") String street, Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByStreet(street, fixedPageable);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter addresses by city and country", description = "Retrieves addresses matching both a city and a country (paginated, 5 per page).")
    public Page<AddressResponse> getByCityAndCountry(
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByCityAndCountry(city, country, fixedPageable);
    }
}
