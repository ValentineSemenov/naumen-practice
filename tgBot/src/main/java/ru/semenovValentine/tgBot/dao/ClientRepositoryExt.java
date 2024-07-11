package ru.semenovValentine.tgBot.dao;

import ru.semenovValentine.tgBot.entity.Client;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@code Client} при помощи кастомных запросов.
 * Данный интерфейс расширяет функционал стандартного репозитория, добавляя методы для выполнения
 * кастомных операций поиска.
 */
public interface ClientRepositoryExt {
    /**
     * Данный метод ищет клиентов по подстроке в их полном имени.
     *
     * @param name Подстрока для поиска клиентов по полному имени.
     * @return Список клиентов, удовлетворяющих условиям поиска.
     */
    List<Client> findClientsByName(String name);
}