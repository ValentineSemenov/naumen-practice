package ru.semenovValentine.tgBot.interfaces;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
/**
 * {@code ITgBotService} представляет интерфейс для обработки запросов от Telegram бота.
 * Этот интерфейс включает методы для обработки callback-запросов и сообщений.
 */
public interface ITgBotService {
    /**
     * Обрабатывает callback-запросы, поступающие от Telegram бота.
     *
     * @param callbackQuery объект {@link CallbackQuery}, содержащий данные callback-запроса.
     * @param bot           объект {@link TelegramBot}, представляющий бота, через которого будет отправлен ответ.
     */
    void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot);
    /**
     * Обрабатывает текстовые сообщения, поступающие от Telegram бота.
     *
     * @param message объект {@link Message}, содержащий данные сообщения.
     * @param bot     объект {@link TelegramBot}, представляющий бота, через которого будет отправлен ответ.
     */
    void handleMessage(Message message, TelegramBot bot);
}