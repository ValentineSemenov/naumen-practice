package ru.semenovValentine.tgBot.telegram.messages;

import java.util.Arrays;
import java.util.Optional;

public enum BaseKeyword {
    START("/start"),
    MAIN_MENU_1("В основное меню"),
    MAIN_MENU_2("Меню"),
    MAIN_MENU_3("/menu"),
    ORDER_INFO("Информация о заказе"),
    PLACE_ORDER("Оформить заказ"),
    RESET_ORDER("Очистить заказ");

    private final String command;

    BaseKeyword(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Optional<BaseKeyword> fromString(String message) {
        return Arrays.stream(values())
                .filter(c -> c.command.equals(message))
                .findFirst();
    }
}
