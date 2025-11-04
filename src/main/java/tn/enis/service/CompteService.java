package tn.enis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.enis.dao.CompteRepository;


import tn.enis.entity.Compte;
import tn.enis.exception.CompteNotFoundException;

@RequiredArgsConstructor

@Service

public class CompteService {

	private final CompteRepository compteRepository;

	public void saveOrUpdate(Compte compte) {

		compteRepository.save(compte);

	}

	public List<Compte> findAll() {

		return compteRepository.findAll();

	}

	public void deleteById(Integer rib) {

		compteRepository.deleteById(rib);

	}

	public Compte findById(Integer rip) {

		return compteRepository.findById(rip)

				.orElseThrow(() -> new CompteNotFoundException("Compte  with rib " + rip + " not found"));

	}
	public List<Compte> findByClientCin(String clientCin) {
    	return compteRepository.findByClientCin(clientCin);
    }

}