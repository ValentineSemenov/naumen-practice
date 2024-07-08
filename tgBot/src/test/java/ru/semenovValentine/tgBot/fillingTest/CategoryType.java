package ru.semenovValentine.tgBot.fillingTest;

public enum CategoryType {
    PIZZA("Pizza"),
    ROLLS("Rolls"),
    BURGERS("Burgers"),
    DRINKS("Drinks");

    private final String name;

    CategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}