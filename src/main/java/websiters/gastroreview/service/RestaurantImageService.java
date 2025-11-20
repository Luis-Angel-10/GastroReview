package websiters.gastroreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import websiters.gastroreview.dto.RestaurantImageRequest;
import websiters.gastroreview.dto.RestaurantImageResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Restaurant;
import websiters.gastroreview.model.RestaurantImage;
import websiters.gastroreview.model.User;
import websiters.gastroreview.repository.RestaurantImageRepository;
import websiters.gastroreview.repository.RestaurantRepository;
import websiters.gastroreview.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantImageService {

    private final RestaurantImageRepository repo;
    private final RestaurantRepository restaurantRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public Page<RestaurantImageResponse> list(Pageable pageable) {
        Pageable fixed = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixed).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantImageResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<RestaurantImage> all = repo.findByRestaurant_Id(restaurantId);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantImageResponse> searchByAltText(String text, Pageable pageable) {
        List<RestaurantImage> all = repo.searchByAltText(text);
        return paginate(all, pageable);
    }

    @Transactional(readOnly = true)
    public RestaurantImageResponse get(UUID id) {
        RestaurantImage img = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant image not found"));
        return Mappers.toResponse(img);
    }

    @Transactional
    public RestaurantImageResponse create(RestaurantImageRequest in) {

        Restaurant restaurant = restaurantRepo.findById(in.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant does not exist"));

        User uploader = null;
        if (in.getUploadedBy() != null) {
            uploader = userRepo.findById(in.getUploadedBy())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploader user not exists"));
        }

        RestaurantImage img = Mappers.toRestaurantImageEntity(in, restaurant, uploader);

        repo.save(img);
        return Mappers.toResponse(img);
    }

    @Transactional
    public RestaurantImageResponse update(UUID id, RestaurantImageRequest in) {
        RestaurantImage img = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant image not found"));

        img.setUrl(in.getUrl());
        img.setAltText(in.getAltText());

        repo.save(img);
        return Mappers.toResponse(img);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant image not found");
        }
        repo.deleteById(id);
    }

    private Page<RestaurantImageResponse> paginate(List<RestaurantImage> list, Pageable pageable) {
        int size = 5;
        int page = pageable.getPageNumber();
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<RestaurantImageResponse> content = list.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), list.size());
    }
}
