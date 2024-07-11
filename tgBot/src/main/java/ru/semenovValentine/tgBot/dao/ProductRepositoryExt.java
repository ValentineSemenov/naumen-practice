package ru.semenovValentine.tgBot.dao;

import ru.semenovValentine.tgBot.entity.Product;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@code Product} при помощи кастомных запросов.
 * Данный интерфейс расширяет функционал стандартного репозитория, добавляя методы для выполнения
 * кастомных операций поиска.
 */
public interface ProductRepositoryExt {
    /**
     * Данный метод ищет продукты по подстроке в их названии.
     *
     * @param name Подстрока для поиска продуктов по названию.
     * @return Список продуктов, удовлетворяющих условиям поиска.
     */
    List<Product> searchProductByName(String name);
}