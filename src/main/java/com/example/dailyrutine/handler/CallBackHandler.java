package com.example.dailyrutine.handler;

import com.example.dailyrutine.service.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * @author Muhammadkomil Murodillayev, сб 23:07. 03/06/23
 */

@Component
public class CallBackHandler {

    private final TelegramService service;

    public CallBackHandler(TelegramService service) {
        this.service = service;
    }

    public void handle(CallbackQuery query) {
        String chatId = query.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String data = query.getData();

        if (data.startsWith("done")) {

            service.done(query, sendMessage);

        } else if (data.startsWith("unfinished")) {
            sendMessage.setText("Ishlab chiqish jarayonida");
            service.sendMessage(sendMessage);
        } else {
            return;
        }
    }

}
