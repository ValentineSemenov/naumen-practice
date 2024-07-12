package ru.semenovValentine.tgBot.rest.service;

import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.dao.ClientOrderRepository;
import ru.semenovValentine.tgBot.entity.Client;
import ru.semenovValentine.tgBot.entity.ClientOrder;

import java.util.Optional;

@Service
public class ClientOrderService {
    private final ClientOrderRepository clientOrderRepository;

    public ClientOrderService(ClientOrderRepository clientOrderRepository) {
        this.clientOrderRepository = clientOrderRepository;
    }

    public Optional<ClientOrder> findActiveByClient(Client client) {
        return clientOrderRepository.findByClientAndStatus(client, 1);
    }

    public void save(ClientOrder clientOrder) {
        clientOrderRepository.save(clientOrder);
    }

    public Optional<ClientOrder> findById(Long id) {
        return clientOrderRepository.findById(id);
    }
}
