package ru.semenovValentine.tgBot.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.entity.Client;
import ru.semenovValentine.tgBot.entity.ClientOrder;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.interfaces.ITelegramKeyboardService;
import ru.semenovValentine.tgBot.rest.service.CategoryService;
import ru.semenovValentine.tgBot.rest.service.ClientOrderService;
import ru.semenovValentine.tgBot.rest.service.ProductService;
import ru.semenovValentine.tgBot.telegram.messages.BaseKeyword;
import ru.semenovValentine.tgBot.telegram.messages.CallbackKeyword;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelegramKeyboardService implements ITelegramKeyboardService {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ClientOrderService clientOrderService;

    public TelegramKeyboardService(CategoryService categoryService, ProductService productService, ClientOrderService clientOrderService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.clientOrderService = clientOrderService;
    }

    public ReplyKeyboardMarkup createBaseKeyboard() {
        List<KeyboardButton> categories = categoryService.getCategoriesByParentId(null)
                .stream()
                .map(category -> new KeyboardButton(category.getName()))
                .collect(Collectors.toList());
        return getReplyKeyboardMarkup(categories);
    }

    public ReplyKeyboardMarkup createSubKeyboard(Long categoryId) {
        List<KeyboardButton> categories = categoryService.getCategoriesByParentId(categoryId)
                .stream()
                .map(category -> new KeyboardButton(category.getName()))
                .collect(Collectors.toList());
        return getReplyKeyboardMarkup(categories);
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup(List<KeyboardButton> categories) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(categories.toArray(new KeyboardButton[0]));
        markup.resizeKeyboard(true);
        markup.addRow(new KeyboardButton(BaseKeyword.MAIN_MENU_1.getCommand()));
        markup.addRow(new KeyboardButton(BaseKeyword.ORDER_INFO.getCommand()));
        markup.addRow(new KeyboardButton(BaseKeyword.PLACE_ORDER.getCommand()));
        return markup;
    }

    public InlineKeyboardMarkup createInlineButtons(Long subCategoryId) {
        List<Product> products = productService.getProductsByCategoryId(subCategoryId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Product product : products) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.format("%s. Цена %.2f $", product.getName(), product.getPrice()))
                    .callbackData(String.format("%s:%d", CallbackKeyword.ADD_PRODUCT.getKeyword(), product.getId()));
            markup.addRow(button);
        }
        return markup;
    }

    public InlineKeyboardMarkup createInlineForCreatedOrder(Client client){
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton(BaseKeyword.ORDER_INFO.getCommand())
                .callbackData(String.format("%s:%d", CallbackKeyword.INFO_ORDER.getKeyword(), clientOrder.getId()));
        markup.addRow(button);
        return markup;
    }

    public InlineKeyboardMarkup createInlineForListOrder(Client client){
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton(BaseKeyword.RESET_ORDER.getCommand())
                .callbackData(String.format("%s:%d",CallbackKeyword.FLUSH_ORDER.getKeyword(), clientOrder.getId()));
        markup.addRow(button);
        return markup;
    }
}