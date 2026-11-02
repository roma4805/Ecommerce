package com.ecommerce.repository;

import java.util.List;
import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;

public class CommandeRepository extends InMemoryRepository<Commande, Long> {
    public CommandeRepository() {
        super(Commande::getId);
    }

    public List<Commande> findByClient(Client client) {
        return findBy(c -> c.getClient().equals(client));
    }
}
