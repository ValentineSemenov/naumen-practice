package ru.semenovValentine.tgBot.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import ru.semenovValentine.tgBot.entity.Product;

import java.util.List;

/**
 Класс является реализацией кастомного репозитория {@code ProductRepositoryExt}.
 Содержит методы для работы с сущностью {@code Product}.
 Для создания динамических запросов используется {@code JPA Criteria API}.
 */
public class ProductRepositoryExtImpl implements ProductRepositoryExt {
    private final EntityManager entityManager;

    @Autowired
    public ProductRepositoryExtImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Product> searchProductByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        String pattern = "%%%s%%";
        cq.select(root).where(cb.like(cb.lower(root.get("name")), pattern.formatted(name).toLowerCase()));

        TypedQuery<Product> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}