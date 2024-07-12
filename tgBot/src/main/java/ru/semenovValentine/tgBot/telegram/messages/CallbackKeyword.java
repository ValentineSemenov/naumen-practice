package ru.semenovValentine.tgBot.telegram.messages;

import java.util.Arrays;
import java.util.Optional;

public enum CallbackKeyword {
    ADD_PRODUCT("add product"),
    INFO_ORDER("info order"),
    FLUSH_ORDER("flush order");

    private final String keyword;

    CallbackKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static Optional<CallbackKeyword> fromString(String message) {
        return Arrays.stream(values())
                .filter(k -> k.keyword.equals(message))
                .findFirst();
    }
}

