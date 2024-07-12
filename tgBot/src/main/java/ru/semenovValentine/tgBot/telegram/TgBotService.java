package ru.semenovValentine.tgBot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.semenovValentine.tgBot.entity.*;
import ru.semenovValentine.tgBot.interfaces.ITgBotService;
import ru.semenovValentine.tgBot.rest.service.*;
import ru.semenovValentine.tgBot.telegram.messages.BaseKeyword;
import ru.semenovValentine.tgBot.telegram.messages.CallbackKeyword;
import ru.semenovValentine.tgBot.telegram.messages.Regexes;

import java.util.*;

@Service
public class TgBotService implements ITgBotService {
    private final String EMPTY_VALUE = "Не указан";
    private final String EMPTY_ORDER = "Заказ пуст.";
    private static final int ORDER_CONFIRMED = 2;
    private static final int ORDER_ACTIVE = 1;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final ClientOrderService clientOrderService;
    private final TelegramKeyboardService keyboard;

    public TgBotService(ClientService clientService, CategoryService categoryService,
                        ProductService productService, OrderProductService orderProductService,
                        ClientOrderService clientOrderService, TelegramKeyboardService keyboard) {
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderProductService = orderProductService;
        this.clientOrderService = clientOrderService;
        this.keyboard = keyboard;
    }
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) {
        Long chatId = callbackQuery.from().id();
        String data = callbackQuery.data();
        // Парсим callback для извлечения ключевого слова
        String callbackKeyword = data.replaceAll(Regexes.NUMS.getRegex(), "");

        try {
            CallbackKeyword.fromString(callbackKeyword).ifPresent(keyword -> {
                switch (keyword) {
                    case ADD_PRODUCT -> handleProductCallback(chatId, data, bot);
                    case INFO_ORDER -> handleOrderInfoCallback(chatId, bot, data);
                    case FLUSH_ORDER -> flushOrder(data);
                }
            });
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, e.toString()));
        }
    }

    public void handleMessage(Message message, TelegramBot bot) {
        Long chatId = message.chat().id();
        Long externalId = message.chat().id();
        String fullName = String.format("%s %s", message.from().firstName(), message.from().lastName());

        //Аргументы кроме externalId нужны только при создании
        Client client = clientService.getOrCreateClient(externalId, fullName, EMPTY_VALUE, EMPTY_VALUE);

        // Если пользователь отправляет номер телефона боту - ловим его в этом if
        if (message.contact() != null) {
            catchClientPhone(message, client);
        }

        if (message.location() != null) {
            catchClientLocation(message, client);
        }

        String text = message.text();

        try {
            BaseKeyword.fromString(text).ifPresentOrElse(command -> {
                switch (command) {
                    case START -> sendStartMessage(chatId, bot);
                    case MAIN_MENU_1, MAIN_MENU_2, MAIN_MENU_3 -> sendMainMenu(chatId, bot);
                    case ORDER_INFO -> sendOrderInfo(chatId, client, bot);
                    case PLACE_ORDER -> placeOrder(chatId, client, bot);
                }
            }, () -> sendCategoryMenu(chatId, text, bot));
        } catch (Exception ignored) {
        }
    }
    private void handleProductCallback(Long chatId, String data, TelegramBot bot) {
        //Получаем всю необходимую информацию для работы с Product-ом из callback`a
        Client client = clientService.getByExternalId(chatId).orElseThrow();
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();

        //Парсим callback для извлечения id продукта
        Long productId = Long.parseLong(data.replaceAll(Regexes.LITERALS.getRegex(), ""));
        Product product = productService.findById(productId).orElseThrow();

        clientOrder.setTotal(clientOrder.getTotal() + product.getPrice());
        clientOrderService.save(clientOrder);

        OrderProduct orderProduct = new OrderProduct(clientOrder, product, 1);
        orderProductService.save(orderProduct);

        String text = String.format("%s. Позиция добавлена в заказ.", product.getName());
        bot.execute(new SendMessage(chatId, text));
    }
    private void sendStartMessage(Long chatId, TelegramBot bot) {
        ReplyKeyboardMarkup markup = keyboard.createBaseKeyboard();
        SendMessage sendMessage = new SendMessage(chatId, "Привет! Я - Бот по доставке еды. Чем могу помочь?")
                .replyMarkup(markup);
        bot.execute(sendMessage);
    }
    private void sendMainMenu(Long chatId, TelegramBot bot) {
        ReplyKeyboardMarkup markup = keyboard.createBaseKeyboard();
        SendMessage sendMessage = new SendMessage(chatId, BaseKeyword.MAIN_MENU_1.getCommand())
                .replyMarkup(markup);
        bot.execute(sendMessage);
    }

    private void placeOrder(Long chatId, Client client, TelegramBot bot){
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();
        if (clientOrder.getTotal() == 0.0) {
            bot.execute(new SendMessage(chatId, EMPTY_ORDER));
            return;
        }

        if (client.getPhoneNumber().equals(EMPTY_VALUE)) {
            KeyboardButton phoneButton = new KeyboardButton("Отправить номер телефона").requestContact(true);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(phoneButton).resizeKeyboard(true);
            SendMessage requestContactMessage = new SendMessage(chatId,
                    "Пожалуйста, отправьте ваш номер телефона.")
                    .replyMarkup(markup);
            bot.execute(requestContactMessage);
            return;
        }

        if(client.getAddress().equals(EMPTY_VALUE)) {
            KeyboardButton addressButton = new KeyboardButton("Отправить адрес").requestLocation(true);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(addressButton).resizeKeyboard(true);
            SendMessage requestContactMessage = new SendMessage(chatId,
                    "Пожалуйста, отправьте свое местоположение.")
                    .replyMarkup(markup);
            bot.execute(requestContactMessage);
            return;
        }

        String text = String.format("Заказ %d подтвержден." +
                " Мы наберем Вас по этому номеру телефона +%s для уточнения информации." +
                " Ожидайте звонок в течение 5 минут.", clientOrder.getId(), client.getPhoneNumber());
        Keyboard markup = keyboard.createInlineForCreatedOrder(client);
        bot.execute(new SendMessage(chatId, text).replyMarkup(markup));

        clientOrder.setStatus(ORDER_CONFIRMED);
        clientOrderService.save(clientOrder);

        ClientOrder newClientOrder = new ClientOrder(client, ORDER_ACTIVE, 0.0);
        clientOrderService.save(newClientOrder);
    }

    private void catchClientPhone(Message message, Client client) {
        String phoneNumber = message.contact().phoneNumber();
        client.setPhoneNumber(phoneNumber);
        clientService.save(client);
    }

    private void catchClientLocation(Message message, Client client) {
        String address = message.location().toString();
        client.setAddress(address);
        clientService.save(client);
    }

    private void sendOrderInfo(Long chatId, Client client, TelegramBot bot) {
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();
        processAndPrintOrderInfo(chatId, bot, clientOrder);
    }

    private void processAndPrintOrderInfo(Long chatId, TelegramBot bot, ClientOrder clientOrder) {
        List<OrderProduct> orderProducts = orderProductService.findByClientOrder(clientOrder);
        Client client = clientService.getByExternalId(chatId).orElseThrow();

        if (orderProducts.isEmpty()) {
            bot.execute(new SendMessage(chatId, EMPTY_ORDER));
            return;
        }

        Map<Product, Integer> productCountMap = new HashMap<>();
        for (OrderProduct orderProduct : orderProducts) {
            productCountMap.merge(orderProduct.getProduct(), orderProduct.getCountProduct(), Integer::sum);
        }

        StringBuilder orderMessage = buildOrderInfoMessage(productCountMap);

        if(clientOrder.getStatus() == ORDER_ACTIVE) {
            Keyboard markup = keyboard.createInlineForListOrder(client);
            SendMessage sendFlushMessage = new SendMessage(chatId, orderMessage.toString()).replyMarkup(markup)
                    .parseMode(ParseMode.Markdown);
            bot.execute(sendFlushMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, orderMessage.toString())
                .parseMode(ParseMode.Markdown);
            bot.execute(sendMessage);
        }
    }

    private StringBuilder buildOrderInfoMessage(Map<Product, Integer> productCountMap) {
        StringBuilder orderMessage = new StringBuilder("*Ваш заказ:*\n");

        double totalPrice = productCountMap.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    int count = entry.getValue();
                    double price = product.getPrice() * count;
                    orderMessage.append(String.format(Locale.US, "*%s*  %d x %.2f = %.2f $\n", product.getName(), count, product.getPrice(), price));
                    return price;
                })
                .reduce(0.0, Double::sum);

        orderMessage.append(String.format(Locale.US, "*Общая стоимость:* %.2f $", totalPrice));
        return orderMessage;
    }

    private void handleOrderInfoCallback(Long chatId, TelegramBot bot, String data) {
        Long id = Long.parseLong(data.replaceAll(Regexes.LITERALS.getRegex(), ""));
        ClientOrder clientOrder = clientOrderService.findById(id).orElseThrow();
        processAndPrintOrderInfo(chatId, bot, clientOrder);
    }

    private void flushOrder(String data) {
        Long id = Long.parseLong(data.replaceAll(Regexes.LITERALS.getRegex(), ""));
        ClientOrder clientOrder = clientOrderService.findById(id).orElseThrow();
        if(clientOrder.getStatus() == ORDER_ACTIVE) {
            orderProductService.delete(clientOrder);
            clientOrder.setTotal(0.0);
            clientOrderService.save(clientOrder);
        }
    }

    private void sendCategoryMenu(Long chatId, String message, TelegramBot bot) {
        Category category = categoryService.findByName(message).orElseThrow();
        Keyboard markup = (category.getParent() != null)
                ? keyboard.createInlineButtons(category.getId())
                : keyboard.createSubKeyboard(category.getId());
        SendMessage sendMessage = new SendMessage(chatId, message).replyMarkup(markup);
        bot.execute(sendMessage);
    }
}