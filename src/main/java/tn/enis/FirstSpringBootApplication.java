package tn.enis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tn.enis.entity.Client;
import tn.enis.entity.Compte;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import tn.enis.dao.ClientRepository;
import tn.enis.dao.CompteRepository;
import tn.enis.entity.Compte;

@SpringBootApplication
public class FirstSpringBootApplication implements CommandLineRunner {


	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private ClientRepository clientRepository;

	public static void main(String[] args) {
		SpringApplication.run(FirstSpringBootApplication.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {
					}

		}
