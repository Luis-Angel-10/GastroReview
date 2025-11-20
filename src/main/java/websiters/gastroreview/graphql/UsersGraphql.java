package websiters.gastroreview.graphql;

import websiters.gastroreview.dto.UserRequest;
import websiters.gastroreview.dto.UserResponse;
import websiters.gastroreview.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class UsersGraphql {

    @Autowired
    private UserService service;

    @QueryMapping
    public List<UserResponse> findAll(@Argument String email) {
        Page<UserResponse> page = service.list(email, PageRequest.of(0, 50));
        return page.getContent();
    }

    @QueryMapping
    public UserResponse findById(@Argument UUID id) {
        return service.get(id);
    }

    @QueryMapping
    public UserResponse findByEmail(@Argument String email) {
        return service.getByEmail(email);
    }

    @MutationMapping
    public UserResponse create(@Valid @Argument UserRequest input) {
        return service.create(input);
    }

    @MutationMapping
    public UserResponse update(@Argument UUID id, @Valid @Argument UserRequest input) {
        return service.update(id, input);
    }

}
