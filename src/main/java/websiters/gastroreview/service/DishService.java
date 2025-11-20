package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.DishRequest;
import websiters.gastroreview.dto.DishResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Dish;
import websiters.gastroreview.model.Restaurant;
import websiters.gastroreview.repository.DishRepository;
import websiters.gastroreview.repository.RestaurantRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository repo;
    private final RestaurantRepository restaurantRepo;

    @Transactional(readOnly = true)
    public Page<DishResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DishResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<Dish> list = repo.findByRestaurant_Id(restaurantId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<DishResponse> searchByName(String name, Pageable pageable) {
        List<Dish> list = repo.findByNameContainingIgnoreCase(name);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public DishResponse get(UUID id) {
        Dish d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        return Mappers.toResponse(d);
    }

    @Transactional
    public DishResponse create(DishRequest in) {

        Restaurant r = restaurantRepo.findById(in.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant not exists"));

        Dish dish = Mappers.toDishEntity(in, r);

        repo.save(dish);
        return Mappers.toResponse(dish);
    }

    @Transactional
    public DishResponse update(UUID id, DishRequest in) {
        Dish d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));

        d.setName(in.getName());
        d.setDescription(in.getDescription());
        d.setPriceCents(in.getPriceCents());
        if (in.getAvailable() != null) d.setAvailable(in.getAvailable());

        repo.save(d);
        return Mappers.toResponse(d);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found");
        }
        repo.deleteById(id);
    }

    private Page<DishResponse> paginate(List<Dish> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<DishResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
