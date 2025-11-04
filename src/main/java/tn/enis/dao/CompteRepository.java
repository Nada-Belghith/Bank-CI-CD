package tn.enis.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enis.entity.Compte;

public interface CompteRepository extends JpaRepository<Compte, Integer>{
	List<Compte> findByClientCin(String cin);

	

}
