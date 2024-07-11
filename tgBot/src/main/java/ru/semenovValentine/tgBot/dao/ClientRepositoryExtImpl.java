package ru.semenovValentine.tgBot.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import ru.semenovValentine.tgBot.entity.Client;

import java.util.List;

/**
    Класс является реализацией кастомного репозитория {@code ClientRepositoryExt}.
    Содержит методы для работы с сущностью {@code Client}.
    Для создания динамических запросов используется {@code JPA Criteria API}.
 */
public class ClientRepositoryExtImpl implements ClientRepositoryExt {
    private final EntityManager entityManager;

    @Autowired
    public ClientRepositoryExtImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Client> findClientsByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);

        String pattern = "%%%s%%";
        cq.select(root).where(cb.like(cb.lower(root.get("fullName")), pattern.formatted(name).toLowerCase()));

        TypedQuery<Client> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}