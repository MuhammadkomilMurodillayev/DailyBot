package com.example.dailyrutine.handler;

import com.example.dailyrutine.entity.Task;
import com.example.dailyrutine.entity.TelegramUser;
import com.example.dailyrutine.enums.DailyType;
import com.example.dailyrutine.enums.State;
import com.example.dailyrutine.enums.VolumeType;
import com.example.dailyrutine.repository.InMemoryRepository;
import com.example.dailyrutine.service.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;

/**
 * @author Muhammadkomil Murodillayev, сб 23:07. 03/06/23
 */

@Component
public class CallBackHandler {

    private final TelegramService telegramService;

    private final InMemoryRepository inMemoryRepository;

    public CallBackHandler(TelegramService telegramService, InMemoryRepository inMemoryRepository) {
        this.telegramService = telegramService;
        this.inMemoryRepository = inMemoryRepository;
    }

    public void handle(CallbackQuery query) {
        String chatId = query.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String data = query.getData();

        if (data.startsWith("done")) {

            telegramService.done(query, sendMessage);

        } else if (data.startsWith("unfinished")) {
            sendMessage.setText("Ishlab chiqish jarayonida");
            telegramService.sendMessage(sendMessage);
        } else if (inMemoryRepository.getState(chatId).equals(State.ADD_TASK_VOLUME_TYPE)
                && Arrays.stream(VolumeType.values())
                .anyMatch(v -> VolumeType.valueOf(data).equals(v))) {
            Task task = inMemoryRepository.getCreateTask(chatId);
            task.setVolumeType(VolumeType.valueOf(data));
            inMemoryRepository.setState(State.ADD_TASK_VOLUME, chatId);
            sendMessage.setText(
                    (VolumeType.valueOf(data).equals(VolumeType.COUNT) ?
                            "Sanoq qiymatini kiriting"
                            :
                            "Qancha minut"));
            telegramService.sendMessage(sendMessage);
        } else if (inMemoryRepository.getState(chatId).equals(State.ADD_TASK_DAILY_TYPE) && Arrays.stream(DailyType.values())
                .anyMatch(v -> data.equals(v.name()))) {
            DailyType dailyType = DailyType.valueOf(data);
            if (dailyType.equals(DailyType.EVERY_CUSTOM)) {
                //TODO add logic for custom type
            } else {
                Task task = inMemoryRepository.getCreateTask(chatId);
                TelegramUser user = telegramService.getUser(chatId);
                task.setDailyType(dailyType);
                task.setUserId(user.getId());
                telegramService.addTask(task);
                inMemoryRepository.removeCreateTask(chatId);
                sendMessage.setText("Task added! 😊");
                telegramService.sendMessage(sendMessage);

            }

        } else {
            return;
        }
    }

}
