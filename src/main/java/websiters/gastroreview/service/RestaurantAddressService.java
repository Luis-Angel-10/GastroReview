package websiters.gastroreview.service;

import websiters.gastroreview.dto.RestaurantAddressRequest;
import websiters.gastroreview.dto.RestaurantAddressResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.RestaurantAddress;
import websiters.gastroreview.model.RestaurantAddressId;
import websiters.gastroreview.repository.AddressRepository;
import websiters.gastroreview.repository.RestaurantAddressRepository;
import websiters.gastroreview.repository.RestaurantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantAddressService {

    private final RestaurantAddressRepository repo;
    private final RestaurantRepository restaurantRepo;
    private final AddressRepository addressRepo;

    public RestaurantAddressService(RestaurantAddressRepository repo,
                                    RestaurantRepository restaurantRepo,
                                    AddressRepository addressRepo) {
        this.repo = repo;
        this.restaurantRepo = restaurantRepo;
        this.addressRepo = addressRepo;
    }

    @Transactional(readOnly = true)
    public Page<RestaurantAddressResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantAddressResponse> findByRestaurant(UUID restaurantId, Pageable pageable) {
        List<RestaurantAddress> all = repo.findByRestaurantId(restaurantId);
        return paginateAndMap(all, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantAddressResponse> findByAddress(UUID addressId, Pageable pageable) {
        List<RestaurantAddress> all = repo.findByAddressId(addressId);
        return paginateAndMap(all, pageable);
    }

    @Transactional(readOnly = true)
    public RestaurantAddressResponse get(UUID restaurantId, UUID addressId) {
        var id = new RestaurantAddressId(restaurantId, addressId);
        var ra = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "RestaurantAddress not found"));
        return Mappers.toResponse(ra);
    }

    @Transactional
    public RestaurantAddressResponse create(RestaurantAddressRequest in) {
        var id = new RestaurantAddressId(in.getRestaurant_Id(), in.getAddress_Id());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Restaurant already linked to this address");
        }

        var restaurant = restaurantRepo.findById(in.getRestaurant_Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        var address = addressRepo.findById(in.getAddress_Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        var ra = RestaurantAddress.builder()
                .id(id)
                .restaurant(restaurant)
                .address(address)
                .isPrimary(Boolean.TRUE.equals(in.getIs_primary()))
                .branchName(in.getBranch_name())
                .build();

        try {
            if (ra.isPrimary()) {
                repo.findByIdRestaurantIdAndIsPrimaryTrue(in.getRestaurant_Id())
                        .ifPresent(prev -> {
                            prev.setPrimary(false);
                            repo.save(prev);
                        });
            }
            ra = repo.saveAndFlush(ra);
            return Mappers.toResponse(ra);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Primary address already set for restaurant");
        }
    }

    @Transactional
    public RestaurantAddressResponse update(UUID restaurantId, UUID addressId, RestaurantAddressRequest in) {
        var id = new RestaurantAddressId(restaurantId, addressId);
        var ra = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "RestaurantAddress not found"));

        if (in.getBranch_name() != null) ra.setBranchName(in.getBranch_name());

        if (in.getIs_primary() != null) {
            if (in.getIs_primary()) {
                final RestaurantAddressId currentId = ra.getId();
                repo.findByIdRestaurantIdAndIsPrimaryTrue(restaurantId)
                        .ifPresent(prev -> {
                            if (!prev.getId().equals(currentId)) {
                                prev.setPrimary(false);
                                repo.save(prev);
                            }
                        });
                ra.setPrimary(true);
            } else {
                ra.setPrimary(false);
            }
        }

        try {
            ra = repo.saveAndFlush(ra);
            return Mappers.toResponse(ra);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Primary address already set for restaurant");
        }
    }

    @Transactional
    public void delete(UUID restaurantId, UUID addressId) {
        var id = new RestaurantAddressId(restaurantId, addressId);
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RestaurantAddress not found");
        }
        repo.deleteById(id);
    }

    private Page<RestaurantAddressResponse> paginateAndMap(List<RestaurantAddress> all, Pageable pageable) {
        int pageSize = 5;
        int currentPage = pageable.getPageNumber();
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, all.size());

        List<RestaurantAddressResponse> content = all.subList(Math.min(start, end), end)
                .stream()
                .map(Mappers::toResponse)
                .toList();

        return new PageImpl<>(content, Pageable.ofSize(pageSize).withPage(currentPage), all.size());
    }
}
