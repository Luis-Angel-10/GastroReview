package websiters.gastroreview.controller;

import websiters.gastroreview.dto.RestaurantScheduleRequest;
import websiters.gastroreview.dto.RestaurantScheduleResponse;
import websiters.gastroreview.service.RestaurantScheduleService;

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
@RequestMapping("/api/restaurant-schedules")
@RequiredArgsConstructor
@Validated
@Tag(name = "RestaurantSchedules", description = "Opening and closing schedules for restaurants.")
public class RestaurantScheduleController {

    private final RestaurantScheduleService service;

    @GetMapping
    @Operation(summary = "List restaurant schedules", description = "Paginated (5 per page) list of restaurant schedules.")
    @ApiResponse(responseCode = "200", description = "Schedules retrieved",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RestaurantScheduleResponse.class))))
    public Page<RestaurantScheduleResponse> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.list(fixed);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "List schedules by restaurant")
    public Page<RestaurantScheduleResponse> byRestaurant(
            @PathVariable UUID restaurantId,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByRestaurant(restaurantId, fixed);
    }

    @GetMapping("/weekday/{weekday}")
    @Operation(summary = "List schedules by weekday", description = "0 = Monday, ..., 6 = Sunday")
    public Page<RestaurantScheduleResponse> byWeekday(
            @PathVariable Integer weekday,
            Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findByWeekday(weekday, fixed);
    }

    @GetMapping("/special")
    @Operation(summary = "List special schedules", description = "Retrieves schedules marked as 'special'.")
    public Page<RestaurantScheduleResponse> special(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return service.findSpecial(fixed);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantScheduleResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public RestaurantScheduleResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create schedule")
    public RestaurantScheduleResponse create(@Validated @RequestBody RestaurantScheduleRequest in) {
        return service.create(in);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update schedule")
    public RestaurantScheduleResponse update(
            @PathVariable UUID id,
            @Validated @RequestBody RestaurantScheduleRequest in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete schedule")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
