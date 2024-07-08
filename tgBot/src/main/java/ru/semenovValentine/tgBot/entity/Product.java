package ru.semenovValentine.tgBot.entity;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Category category;

    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @Column(length = 400, nullable = false)
    private String description;

    @Column(length = 15, precision = 2, nullable = false)
    private Double price;

    public Product(Category category, String name, String description, Double price) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
