package com.example.dailyrutine;

import com.example.dailyrutine.handler.CallBackHandler;
import com.example.dailyrutine.handler.MessageHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Muhammadkomil Murodillayev, сб 12:11. 03/06/23
 */

@Component
public class Bot extends TelegramLongPollingBot {

    private final CallBackHandler callBackHandler;

    private final MessageHandler messageHandler;

    public Bot(@Lazy CallBackHandler callBackHandler,@Lazy MessageHandler messageHandler) {
        this.callBackHandler = callBackHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            messageHandler.handle(update.getMessage());
        } else if (update.hasCallbackQuery()) {

            callBackHandler.handle(update.getCallbackQuery());
        } else {

            return;
        }
    }

    @Override
    public String getBotUsername() {
        return "my_daily_rutine_bot";
    }

    @Override
    public String getBotToken() {
        return "6229237423:AAHdKYNGD_5sswmvfmb0N9zPS_TXFA8D_7M";
    }

    public int sendMessage(SendMessage sendMessage) {
        try {
            return execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(int messageId, String chatId) {
        try {
            execute(DeleteMessage.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
