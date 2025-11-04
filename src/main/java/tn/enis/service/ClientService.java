package tn.enis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.enis.dao.ClientRepository;
import tn.enis.entity.Client;
import tn.enis.exception.*;
@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository ;

    public void saveOrUpdate(Client client) {
        clientRepository.save(client);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void deleteById(String cin) {
        clientRepository.deleteById(cin);
    }

    public Client findById(String cin) {
        return clientRepository.findById(cin)
            .orElseThrow(() -> new ClientNotFoundException("Client with CIN " + cin + " was not found"));
    }
}
