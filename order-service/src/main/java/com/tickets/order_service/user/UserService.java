package com.tickets.order_service.user;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User register(String email, String name) {
        if (repository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }
        return repository.save(new User(email, name));
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}