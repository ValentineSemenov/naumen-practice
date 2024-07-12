package ru.semenovValentine.tgBot.interfaces;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

/**
 * {@code ITelegramKeyboardService} представляет интерфейс для создания клавиатур в {@code tgBot}
 */
public interface ITelegramKeyboardService {
    /**
     * Создаёт клавиатуру с базовыми категориями.
     *
     * @return ReplyKeyboardMarkup представляет базовую клавиатуру.
     */
    ReplyKeyboardMarkup createBaseKeyboard();

    /**
     * Создаёт клавиатуру подкатегорий
     *
     * @param categoryId Id базовой категории, чьи подкатегории должны быть выведены в виде кнопок.
     * @return ReplyKeyboardMarkup представляет клавиатуру подкатегорий.
     */
    ReplyKeyboardMarkup createSubKeyboard(Long categoryId);

    /**
     * Создаёт инлайн кнопки в соответствии с каждым продуктом в подкатегории.
     * В каждой инлайн кнопке выводит название продукта и его цену.
     * Назначает callback код в виде "add product:<id>".
     *
     * @param subCategoryId Id подкатегории, продукты которой мы должны вывести
     * @return InlineKeyboardMarkup представляет инлайн клавиатуру.
     */
    InlineKeyboardMarkup createInlineButtons(Long subCategoryId);
}
