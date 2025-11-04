package tn.enis.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.entity.Client;


public interface ClientRepository extends JpaRepository<Client, String> {

}
