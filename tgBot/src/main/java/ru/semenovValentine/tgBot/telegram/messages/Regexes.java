package ru.semenovValentine.tgBot.telegram.messages;

public enum Regexes {
    NUMS("[0-9:]"),
    LITERALS("[^0-9]");

    Regexes(String regex) {
        this.regex = regex;
    }

    private final String regex;

    public String getRegex() {
        return regex;
    }
}
