package com.example.dailyrutine.handler;

import com.example.dailyrutine.service.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Muhammadkomil Murodillayev, сб 22:00. 03/06/23
 */

@Component
public class MessageHandler {
    private final TelegramService telegramService;

    public MessageHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public void handle(Message message) {
        String chatId = message.getChatId().toString();
        String text = message.getText();
        int messageId = message.getMessageId();

        if (text.equalsIgnoreCase("/start")) {
            telegramService.sendMessage(SendMessage
                    .builder()
                    .chatId(chatId)
                    .text("Assalomu alaykum va rohmatullohi va barokatuh!😊")
                    .build());
        } else if (text.endsWith("#active")) {
            telegramService.sendMessage(SendMessage.builder().chatId(chatId)
                    .text("Vazifalarni tashlab borish funksiyasi aktivlashdi").build());
            telegramService.saveUser(message);
        } else if (text.endsWith("#stop")) {
            telegramService.sendMessage(SendMessage.builder().chatId(chatId)
                    .text("Vazifalarni tashlab borish funksiyasi to'xtatildi").build());
        } else if (text.endsWith("#tasks")) {
            telegramService.sendTasks(text.substring(0, text.indexOf("#")));
        }

        telegramService.deleteMessage(messageId, chatId);

    }
}
