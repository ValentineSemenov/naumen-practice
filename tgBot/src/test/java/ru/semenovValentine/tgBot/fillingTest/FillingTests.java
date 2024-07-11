package ru.semenovValentine.tgBot.fillingTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.semenovValentine.tgBot.entity.*;
import ru.semenovValentine.tgBot.dao.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FillingTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private void saveCategory(String name, Category parent) {
        Category category = new Category(name, parent);
        categoryRepository.save(category);
    }

    private void saveProduct(Category category, String name, String description, double price) {
        Product product = new Product(category, name, description, price);
        productRepository.save(product);
    }

    @BeforeAll
    void setUp() {
        createBaseCategories();
        createRollsCategories();
        createBurgersCategories();
        createDrinksCategories();
        createPizzaCategories();
    }

    private void createBaseCategories(){
        saveCategory(CategoryType.PIZZA.getName(), null);
        saveCategory(CategoryType.ROLLS.getName(), null);
        saveCategory(CategoryType.BURGERS.getName(), null);
        saveCategory(CategoryType.DRINKS.getName(), null);
    }

    private void createRollsCategories(){
        Category rolls = categoryRepository.findCategoryByName(CategoryType.ROLLS.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        saveCategory("Classical Rolls", rolls);
        saveCategory("Baked Rolls", rolls);
        saveCategory("Sweet Rolls", rolls);
        saveCategory("Sets", rolls);
    }

    private void createBurgersCategories(){
        Category burgers = categoryRepository.findCategoryByName(CategoryType.BURGERS.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        saveCategory("Classical Burgers", burgers);
        saveCategory("Spicy Burgers", burgers);
        saveCategory("Fancy Burgers", burgers);
    }

    private void createDrinksCategories(){
        Category drinks = categoryRepository.findCategoryByName(CategoryType.DRINKS.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        saveCategory("Carbonated Drinks", drinks);
        saveCategory("Energetic Drinks", drinks);
        saveCategory("Juices", drinks);
        saveCategory("Others", drinks);
    }

    private void createPizzaCategories(){
        Category pizzas = categoryRepository.findCategoryByName(CategoryType.PIZZA.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        saveCategory("Italian pizza", pizzas);
        saveCategory("American pizza", pizzas);
        saveCategory("Other", pizzas);
    }

    @Test
    void fillingCategories(){
        fillingRollCategory();
        fillingBurgerCategory();
        fillingDrinkCategory();
        fillingPizzaCategory();
  }

    void fillingRollCategory(){
        Category bakedRolls = categoryRepository.findCategoryByName("Baked Rolls").orElseThrow();
        saveProduct(bakedRolls, "Okami", "Baked rolls", 10.2);
        saveProduct(bakedRolls, "Hatico", "Baked rolls", 9.0);
        saveProduct(bakedRolls, "Saimon", "Baked rolls", 11.0);

        Category sweetRolls = categoryRepository.findCategoryByName("Sweet Rolls").orElseThrow();
        saveProduct(sweetRolls, "Panko", "Sweet rolls", 3.22);
        saveProduct(sweetRolls, "Danko", "Sweet rolls", 4.2);
        saveProduct(sweetRolls, "Tanko", "Sweet rolls", 5.62);

        Category sets = categoryRepository.findCategoryByName("Sets").orElseThrow();
        saveProduct(sets, "Red Dragon", "Sets", 30.0);
        saveProduct(sets, "Black Dragon", "Sets", 40.0);
        saveProduct(sets, "White Dragon", "Sets", 40.0);
    }

    void fillingBurgerCategory(){
        Category classicBurger = categoryRepository.findCategoryByName("Classical Burgers").orElseThrow();
        saveProduct(classicBurger, "Strong", "Classical Burger", 8.5);
        saveProduct(classicBurger, "Rock", "Rock Burger", 10.0);
        saveProduct(classicBurger, "Fancy", "Fancy Burger", 20.0);

        Category spicyBurger = categoryRepository.findCategoryByName("Spicy Burgers").orElseThrow();
        saveProduct(spicyBurger, "Karolina Reaper", "Hottest Pepper in the world", 302.50);
        saveProduct(spicyBurger, "Scoville King", "1000 Scoville bomb", 10.0);
        saveProduct(spicyBurger, "Hot", "How trip", 20.62);

        Category fancyBurger = categoryRepository.findCategoryByName("Fancy Burgers").orElseThrow();
        saveProduct(fancyBurger, "Gold Burger", "999 burger", 999.99);
        saveProduct(fancyBurger, "Silver Burger", "True silver burger", 925.25);
        saveProduct(fancyBurger, "Platinum Burger", "All from platinum burger!!!", 1000.0);
    }

    void fillingDrinkCategory(){
        Category carbonatedDrinks = categoryRepository.findCategoryByName("Carbonated Drinks").orElseThrow();
        saveProduct(carbonatedDrinks, "Borjomi", "Carbonated Water", 10.2);
        saveProduct(carbonatedDrinks, "bonaqua", "Carbonated Water", 9.0);
        saveProduct(carbonatedDrinks, "Coca Cola", "Soda", 11.0);

        Category energeticsDrinks = categoryRepository.findCategoryByName("Energetic Drinks").orElseThrow();
        saveProduct(energeticsDrinks, "Red Bull", "Original flavor", 5.22);
        saveProduct(energeticsDrinks, "Adrenaline", "Zero sugar", 3.2);
        saveProduct(energeticsDrinks, "Monster", "Coconut flavor", 10.62);

        Category juices = categoryRepository.findCategoryByName("Juices").orElseThrow();
        saveProduct(juices, "Orange Juice", "Juice", 10.0);
        saveProduct(juices, "Cherry Juice", "Juice", 10.0);
        saveProduct(juices, "Apple Juice", "Juice", 10.0);

        Category other = categoryRepository.findCategoryByName("Others").orElseThrow();
        saveProduct(other, "Leffe Blonde", "Bear", 31.64);
        saveProduct(other, "Artesian Water", "Water", 100.0);
        saveProduct(other, "Your Drinks", "Сorkage fee. 100 dollars per fee", 100.0);
    }

    void fillingPizzaCategory(){
        Category italianPizza = categoryRepository.findCategoryByName("Italian pizza").orElseThrow();
        saveProduct(italianPizza, "Neapolitan pizza", "Neapolitan pizza", 15.5);
        saveProduct(italianPizza, "Margarita", "Margarita", 15.5);
        saveProduct(italianPizza, "Italian classic", "Italian classic pizza", 15.5);

        Category americanPizza = categoryRepository.findCategoryByName("American pizza").orElseThrow();
        saveProduct(americanPizza, "New York Pizza", "The best New York pizza", 10.50);
        saveProduct(americanPizza, "Chicago Pizza", "Gangsters Chicago pizza", 10.0);
        saveProduct(americanPizza, "Los Angeles Pizza", "Angeles pizza", 12.63);

        Category other = categoryRepository.findCategoryByName("Other").orElseThrow();
        saveProduct(other, "Vegan Pizza", "Vegan pizza", 12.99);
        saveProduct(other, "Vegetarian Pizza", "Vegetarian pizza", 14.25);
        saveProduct(other, "Sweet Pizza", "Too sweet for desert - taste it!", 15.0);
    }
}

