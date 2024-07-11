package ru.semenovValentine.tgBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.semenovValentine.tgBot.entity.Product;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends
        JpaRepository<Product, Long>,
        ProductRepositoryExt {
    List<Product> findByCategoryId(Long id);
}