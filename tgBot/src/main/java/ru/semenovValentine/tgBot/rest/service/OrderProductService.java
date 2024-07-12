package ru.semenovValentine.tgBot.rest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semenovValentine.tgBot.dao.OrderProductRepository;
import ru.semenovValentine.tgBot.entity.ClientOrder;
import ru.semenovValentine.tgBot.entity.OrderProduct;
import ru.semenovValentine.tgBot.entity.Product;

import java.util.List;
import java.util.Optional;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @Transactional
    public void save(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }

    public List<OrderProduct> findByClientOrder(ClientOrder clientOrder){
        return orderProductRepository.findByClientOrder(clientOrder);
    }

    @Transactional
    public void delete(ClientOrder clientOrder) {
        orderProductRepository.deleteAllByClientOrder(clientOrder);
    }
}
