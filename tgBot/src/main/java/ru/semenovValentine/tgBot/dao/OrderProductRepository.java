package ru.semenovValentine.tgBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.semenovValentine.tgBot.entity.OrderProduct;
import ru.semenovValentine.tgBot.entity.Product;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "orderProducts", path = "orderProducts")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query("SELECT op.product FROM OrderProduct op " +
            "WHERE op.clientOrder.client.id = :id")
    List<Product> getClientProducts(Long id);

    @Query("SELECT product FROM OrderProduct op " +
            "GROUP BY op.product " +
            "ORDER BY SUM(op.countProduct) DESC " +
            "LIMIT :limit")
    List<Product> findTopPopularProducts(Integer limit);
}