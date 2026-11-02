package com.ecommerce.repository;

import java.util.Optional;
import com.ecommerce.model.Client;

public class ClientRepository extends InMemoryRepository<Client, Long> {
    public ClientRepository() {
        super(Client::getId);
    }

    public Optional<Client> findByEmail(String email) {
        return findAll().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
