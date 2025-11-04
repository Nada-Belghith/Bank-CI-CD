package tn.enis.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tn.enis.entity.Client;

@Repository
public class ClientDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Client client) {
        entityManager.persist(client);
    }

    public void update(Client client) {
        entityManager.merge(client);
    }

    public void delete(Client client) {
        entityManager.remove(client);
    }

    public Client findById(String id) {
        return entityManager.find(Client.class, id);
    }

    public List<Client> findAll() {
        return entityManager.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }
}
