package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.FavoriteRestaurantRequest;
import websiters.gastroreview.dto.FavoriteRestaurantResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.*;
import websiters.gastroreview.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteRestaurantService {

    private final FavoriteRestaurantRepository repo;
    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;

    @Transactional(readOnly = true)
    public Page<FavoriteRestaurantResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteRestaurantResponse> findByUser(UUID userId, Pageable pageable) {
        List<FavoriteRestaurant> list = repo.findByUser_Id(userId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteRestaurantResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<FavoriteRestaurant> list = repo.findByRestaurant_Id(restaurantId);
        return paginate(list, pageable);
    }

    @Transactional(readOnly = true)
    public FavoriteRestaurantResponse get(UUID userId, UUID restaurantId) {
        FavoriteRestaurantId id = FavoriteRestaurantId.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .build();

        FavoriteRestaurant fav = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        return Mappers.toResponse(fav);
    }

    @Transactional
    public FavoriteRestaurantResponse create(FavoriteRestaurantRequest in) {

        if (repo.existsByUser_IdAndRestaurant_Id(in.getUserId(), in.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already marked as favorite");
        }

        User user = userRepo.findById(in.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"));

        Restaurant restaurant = restaurantRepo.findById(in.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant does not exist"));

        FavoriteRestaurant fav = Mappers.toFavoriteRestaurantEntity(in, user, restaurant);
        repo.save(fav);

        return Mappers.toResponse(fav);
    }

    @Transactional
    public void delete(UUID userId, UUID restaurantId) {
        FavoriteRestaurantId id = new FavoriteRestaurantId(userId, restaurantId);

        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }

        repo.deleteById(id);
    }

    private Page<FavoriteRestaurantResponse> paginate(List<FavoriteRestaurant> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<FavoriteRestaurantResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
