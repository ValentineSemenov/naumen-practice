package ru.semenovValentine.tgBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.semenovValentine.tgBot.entity.Client;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends
        JpaRepository<Client, Long>,
        ClientRepositoryExt {
}