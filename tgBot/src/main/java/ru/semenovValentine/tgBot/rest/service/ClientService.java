package ru.semenovValentine.tgBot.rest.service;

import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.dao.ClientRepository;
import ru.semenovValentine.tgBot.entity.Client;
import ru.semenovValentine.tgBot.entity.ClientOrder;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.dao.ClientOrderRepository;
import ru.semenovValentine.tgBot.dao.OrderProductRepository;

import java.util.List;

@Service
public class ClientService {
    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ClientRepository clientRepository;

    public ClientService(ClientOrderRepository clientOrderRepository, OrderProductRepository orderProductRepository, ClientRepository clientRepository) {
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
        this.clientRepository = clientRepository;
    }

    public List<ClientOrder> getClientOrders(Long id){
        return clientOrderRepository.findByClientId(id);
    }

    public List<Product> getClientProducts(Long id){
        return orderProductRepository.getClientProducts(id);
    }

    public List<Client> searchClientsByName(String name) {
        return clientRepository.findClientsByName(name);
    }
}