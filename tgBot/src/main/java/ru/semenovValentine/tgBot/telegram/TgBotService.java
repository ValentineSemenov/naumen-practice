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

import java.util.*;

@Service
public class TgBotService implements ITgBotService {
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final ClientOrderService clientOrderService;
    private final TelegramKeyboardService keyboard;

    public TgBotService(ClientService clientService, CategoryService categoryService, ProductService productService, OrderProductService orderProductService, ClientOrderService clientOrderService, ru.semenovValentine.tgBot.telegram.TelegramKeyboardService keyboard) {
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
        String callbackKeyword = data.replaceAll("[0-9:]", "");

        try {
            switch (callbackKeyword) {
                case "add product" -> handleProductCallback(chatId, data, bot);
                case "info order" -> handleOrderInfoCallback(chatId, bot, data);
                case "flush order" -> flushOrder(data);
            }
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, e.toString()));
        }
    }

    public void handleMessage(Message message, TelegramBot bot) {
        Long chatId = message.chat().id();
        Long externalId = message.chat().id();
        String fullName = String.format("%s %s", message.from().firstName(), message.from().lastName());

        //Аргументы кроме externalId нужны только при создании
        Client client = clientService.getOrCreateClient(externalId, fullName, "Не указан", "Не указан");

        // Если пользователь отправляет номер телефона боту - ловим его в этом if
        if (message.contact() != null) {
            catchClientPhone(message, client);
        }

        if (message.location() != null) {
            catchClientLocation(message, client);
        }

        String text = message.text();
        try {
            switch (text) {
                case "/start" -> sendStartMessage(chatId, bot);
                case "В основное меню", "Меню", "/menu" -> sendMainMenu(chatId, bot);
                case "Информация о заказе" -> sendOrderInfo(chatId, client, bot);
                case "Оформить заказ" -> placeOrder(chatId, client, bot);
                //логика работы с категориями в default
                default -> sendCategoryMenu(chatId, text, bot);
            }
        } catch (Exception ignored) {
        }
    }
    private void handleProductCallback(Long chatId, String data, TelegramBot bot) {
        //Получаем всю необходимую информацию для работы с Product-ом из callback`a
        Client client = clientService.getByExternalId(chatId).orElseThrow();
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();

        //Парсим callback для извлечения id продукта
        Long productId = Long.parseLong(data.replaceAll("[^0-9]", ""));
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
        SendMessage sendMessage = new SendMessage(chatId, "Основное меню")
                .replyMarkup(markup);
        bot.execute(sendMessage);
    }

    private void placeOrder(Long chatId, Client client, TelegramBot bot){
        ClientOrder clientOrder = clientOrderService.findActiveByClient(client).orElseThrow();
        if (clientOrder.getTotal() == 0.0) {
            bot.execute(new SendMessage(chatId, "Заказ пуст."));
            return;
        }

        if (client.getPhoneNumber().equals("Не указан")) {
            KeyboardButton phoneButton = new KeyboardButton("Отправить номер телефона").requestContact(true);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(phoneButton).resizeKeyboard(true);
            SendMessage requestContactMessage = new SendMessage(chatId,
                    "Пожалуйста, отправьте ваш номер телефона.")
                    .replyMarkup(markup);
            bot.execute(requestContactMessage);
            return;
        }

        if(client.getAddress().equals("Не указан")) {
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

        clientOrder.setStatus(2);
        clientOrderService.save(clientOrder);

        ClientOrder newClientOrder = new ClientOrder(client, 1, 0.0);
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
            bot.execute(new SendMessage(chatId, "Заказ пуст."));
            return;
        }

        Map<Product, Integer> productCountMap = new HashMap<>();
        for (OrderProduct orderProduct : orderProducts) {
            productCountMap.merge(orderProduct.getProduct(), orderProduct.getCountProduct(), Integer::sum);
        }

        StringBuilder orderMessage = buildOrderInfoMessage(productCountMap);

        if(clientOrder.getStatus() == 1) {
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
        Long id = Long.parseLong(data.replaceAll("[^0-9]", ""));
        ClientOrder clientOrder = clientOrderService.findById(id).orElseThrow();
        processAndPrintOrderInfo(chatId, bot, clientOrder);
    }

    private void flushOrder(String data) {
        Long id = Long.parseLong(data.replaceAll("[^0-9]", ""));
        ClientOrder clientOrder = clientOrderService.findById(id).orElseThrow();
        if(clientOrder.getStatus() == 1) {
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