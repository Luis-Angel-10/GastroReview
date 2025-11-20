package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.RestaurantScheduleRequest;
import websiters.gastroreview.dto.RestaurantScheduleResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Restaurant;
import websiters.gastroreview.model.RestaurantSchedule;
import websiters.gastroreview.repository.RestaurantRepository;
import websiters.gastroreview.repository.RestaurantScheduleRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantScheduleService {

    private final RestaurantScheduleRepository repo;
    private final RestaurantRepository restaurantRepo;

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<RestaurantSchedule> list = repo.findByRestaurant_Id(restaurantId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResponse> findByWeekday(Integer weekday, Pageable pageable) {
        List<RestaurantSchedule> list = repo.findByWeekday(weekday);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResponse> findSpecial(Pageable pageable) {
        List<RestaurantSchedule> list = repo.findSpecialSchedules();
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public RestaurantScheduleResponse get(UUID id) {
        RestaurantSchedule rs = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        return Mappers.toResponse(rs);
    }

    @Transactional
    public RestaurantScheduleResponse create(RestaurantScheduleRequest in) {

        Restaurant restaurant = restaurantRepo.findById(in.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant does not exist"));

        RestaurantSchedule rs = Mappers.toRestaurantScheduleEntity(in, restaurant);
        repo.save(rs);

        return Mappers.toResponse(rs);
    }

    @Transactional
    public RestaurantScheduleResponse update(UUID id, RestaurantScheduleRequest in) {
        RestaurantSchedule rs = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        rs.setWeekday(in.getWeekday());
        rs.setOpenTime(in.getOpenTime());
        rs.setCloseTime(in.getCloseTime());
        if (in.getSpecial() != null) rs.setSpecial(in.getSpecial());

        repo.save(rs);

        return Mappers.toResponse(rs);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
        }
        repo.deleteById(id);
    }

    private Page<RestaurantScheduleResponse> paginate(List<RestaurantSchedule> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<RestaurantScheduleResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
