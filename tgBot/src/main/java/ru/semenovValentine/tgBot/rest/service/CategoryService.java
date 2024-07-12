package ru.semenovValentine.tgBot.rest.service;

import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.dao.CategoryRepository;
import ru.semenovValentine.tgBot.entity.Category;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategoriesByParentId(Long id) {
        return categoryRepository.findCategoriesByParentId(id);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findCategoryByName(name);
    }
}
