package websiters.gastroreview.service;

import websiters.gastroreview.dto.CategoryRequest;
import websiters.gastroreview.dto.CategoryResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.RestaurantCategory;
import websiters.gastroreview.repository.RestaurantCategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository repo;

    public RestaurantCategoryService(RestaurantCategoryRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        Pageable fixedPageable = Pageable.ofSize(5).withPage(pageable.getPageNumber());
        return repo.findAll(fixedPageable).map(Mappers::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Integer id) {
        RestaurantCategory c = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return Mappers.toResponse(c);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest in) {
        if (repo.existsByNameIgnoreCase(in.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }

        RestaurantCategory c = RestaurantCategory.builder()
                .name(in.getName())
                .icon(in.getIcon())
                .build();

        try {
            c = repo.save(c);
            return Mappers.toResponse(c);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest in) {
        RestaurantCategory c = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (in.getName() != null && !in.getName().equalsIgnoreCase(c.getName())) {
            if (repo.existsByNameIgnoreCaseAndIdNot(in.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
            }
            c.setName(in.getName());
        }

        if (in.getIcon() != null) c.setIcon(in.getIcon());

        try {
            c = repo.save(c);
            return Mappers.toResponse(c);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        repo.deleteById(id);
    }
}
