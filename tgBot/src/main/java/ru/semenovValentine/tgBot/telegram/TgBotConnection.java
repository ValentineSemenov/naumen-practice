package ru.semenovValentine.tgBot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TgBotConnection {
    private final TelegramBot bot;
    private final TgBotService tgBotService;

    public TgBotConnection(@Value("${telegram.bot.token}") TelegramBot bot, TgBotService tgBotService) {
        this.bot = bot;
        this.tgBotService = tgBotService;
    }

    @PostConstruct
    private void start() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        if (update.callbackQuery() != null) {
            tgBotService.handleCallbackQuery(update.callbackQuery(), bot);
        } else if (update.message() != null) {
            tgBotService.handleMessage(update.message(), bot);
        }
    }
}