package com.example.dailyrutine.handler;

import com.example.dailyrutine.button.MarkupBoard;
import com.example.dailyrutine.entity.Task;
import com.example.dailyrutine.entity.TelegramUser;
import com.example.dailyrutine.enums.State;
import com.example.dailyrutine.enums.VolumeType;
import com.example.dailyrutine.repository.InMemoryRepository;
import com.example.dailyrutine.service.TelegramService;
import com.example.dailyrutine.utils.Util;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Muhammadkomil Murodillayev, сб 22:00. 03/06/23
 */

@Component
public class MessageHandler {
    private final TelegramService telegramService;

    private final InMemoryRepository inMemoryRepository;

    private final MarkupBoard markupBoard;

    public MessageHandler(TelegramService telegramService, InMemoryRepository inMemoryRepository, MarkupBoard markupBoard) {
        this.telegramService = telegramService;
        this.inMemoryRepository = inMemoryRepository;
        this.markupBoard = markupBoard;
    }

    public void handle(Message message) {
        String chatId = message.getChatId().toString();
        String text = message.getText();
        int messageId = message.getMessageId();

        if (text.equalsIgnoreCase("/start")) {
            telegramService.sendMessage(SendMessage.builder().chatId(chatId).text("Assalomu alaykum va rohmatullohi va barokatuh!😊").build());

        } else if (text.endsWith("#active")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Vazifalarni tashlab borish funksiyasi aktivlashdi");
            sendMessage.setChatId(chatId);
            markupBoard.mainMenu(sendMessage);
            telegramService.sendMessage(sendMessage);
            telegramService.saveUser(message);

        } else if (text.endsWith("#stop")) {
            telegramService.sendMessage(SendMessage.builder().chatId(chatId).text("Vazifalarni tashlab borish funksiyasi to'xtatildi").build());

        } else if (text.endsWith("#tasks")) {
            telegramService.sendTasks(text.substring(0, text.indexOf("#")));

        } else if (text.equalsIgnoreCase("add task")) {
            inMemoryRepository.setState(State.ADD_TASK_NAME, chatId);
            telegramService.sendMessage(SendMessage.builder().text("Vazifa mazmunini kiriting").chatId(chatId).build());

        } else if (text.equalsIgnoreCase("new tasks")) {
            TelegramUser user = telegramService.getUser(chatId);
            telegramService.sendTasks(user.getKey());

        } else if (inMemoryRepository.getState(chatId).equals(State.ADD_TASK_NAME)) {
            Task task = new Task();
            task.setName(text);
            task = telegramService.addTask(task);
            inMemoryRepository.setCreateTask(task, chatId);
            SendMessage sendMessage = new SendMessage(chatId, "Vazifani tashlab borish turini tanlang");
            markupBoard.volumeTypes(sendMessage);
            inMemoryRepository.setState(State.ADD_TASK_VOLUME_TYPE, chatId);
            telegramService.sendMessage(sendMessage);

        } else if (inMemoryRepository.getState(chatId).equals(State.ADD_TASK_VOLUME)) {
            if (Util.isDigit(text)) {
                Task task = inMemoryRepository.getCreateTask(chatId);
                SendMessage sendMessage = new SendMessage(chatId, "Vazifani tashlab borish rejimini tanlang");
                if (task.getVolumeType().equals(VolumeType.COUNT)) {
                    task.setPlanCount(Integer.parseInt(text));
                } else task.setPlanMin(Long.parseLong(text));
                markupBoard.dailyTypes(sendMessage);
                inMemoryRepository.setState(State.ADD_TASK_DAILY_TYPE, chatId);
                telegramService.sendMessage(sendMessage);

            } else {
                telegramService.sendMessage(SendMessage.builder().chatId(chatId).text("Iltimos raqam kiriting").build());

            }
        }
        telegramService.deleteMessage(messageId, chatId);


    }
}
