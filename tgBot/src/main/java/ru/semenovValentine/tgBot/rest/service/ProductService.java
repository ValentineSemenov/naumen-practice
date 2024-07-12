package ru.semenovValentine.tgBot.rest.service;

import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.dao.OrderProductRepository;
import ru.semenovValentine.tgBot.dao.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public ProductService(ProductRepository productRepository, OrderProductRepository orderProductRepository) {
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public List<Product> getProductsByCategoryId(Long id) {
        return productRepository.findByCategoryId(id);
    }

    public List<Product> getTopPopularProducts(Integer limit) {
        return orderProductRepository.findTopPopularProducts(limit);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.searchProductByName(name);
    }

    public List<Product> getContains(List<Product> list1, List<Product> list2) {
        // Находим пересечение продуктов по категории и имени
        return list1.stream()
                .filter(list2::contains)
                .toList();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}