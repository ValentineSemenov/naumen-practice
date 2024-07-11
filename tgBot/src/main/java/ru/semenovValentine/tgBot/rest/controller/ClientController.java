package ru.semenovValentine.tgBot.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.semenovValentine.tgBot.entity.Client;
import ru.semenovValentine.tgBot.entity.ClientOrder;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.rest.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("rest/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("{id}/orders")
    List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return clientService.getClientOrders(id);
    }

    @GetMapping("{id}/products")
    List<Product> getClientProducts(@PathVariable Long id){
        return clientService.getClientProducts(id);
    }

    @GetMapping("/search")
    List<Client> searchClientsByName(@RequestParam("name") String name){
        return clientService.searchClientsByName(name);
    }
}