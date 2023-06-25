package com.example.dailyrutine.service;

import com.example.dailyrutine.Bot;
import com.example.dailyrutine.button.MarkupBoard;
import com.example.dailyrutine.entity.History;
import com.example.dailyrutine.entity.Task;
import com.example.dailyrutine.entity.TelegramUser;
import com.example.dailyrutine.enums.VolumeType;
import com.example.dailyrutine.repository.HistoryRepository;
import com.example.dailyrutine.repository.InMemoryRepository;
import com.example.dailyrutine.repository.TaskRepository;
import com.example.dailyrutine.repository.TelegramUserRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Muhammadkomil Murodillayev, сб 18:03. 03/06/23
 */
@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TaskRepository taskRepository;
    private final HistoryRepository historyRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final InMemoryRepository inMemoryRepository;
    private final MarkupBoard markupBoard;
    private final Bot bot;

    @Scheduled(cron = "0 0 0 * * *")
    private void sendTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TelegramUser> users = telegramUserRepository.findAll();

        tasks.forEach(task -> {
            writeHistory(task);
            restart(task);
        });

        users.forEach(user -> {
            inMemoryRepository.removeAllMessage(user.getChatId());
            sendTasks(user.getKey());
        });

    }

    private void restart(Task task) {
        task.setDone(false);
        task.setSpentCount(0);
        task.setSpentMin(0L);
        task.setUpdatedAt(LocalDateTime.now());
    }

    private void writeHistory(Task task) {

        if (task.getCreatedAt().getDayOfMonth() == task.getUpdatedAt().getDayOfMonth()) {
            return;
        }

        History history = null;
        Optional<History> historyOptional = historyRepository.findByTaskId(task.getId());

        if (historyOptional.isEmpty()) {
            history = new History();
            history.setTaskId(task.getId());
//            history.setTotalPlanCount(0L);
//            history.setTotalPlanMin(0L);
//            history.setTotalSpentCount(0L);
//            history.setTotalSpentMin(0L);
            history = historyRepository.save(history);
        }
        assert history != null;
        if (task.getVolumeType().equals(VolumeType.COUNT)) {
            history.setTotalPlanCount(history.getTotalPlanCount() + task.getPlanCount());
            history.setTotalSpentCount(history.getTotalSpentCount() + task.getSpentCount());
        } else {
            history.setTotalPlanMin(history.getTotalPlanMin() + task.getPlanMin());
            history.setTotalSpentMin(history.getTotalSpentMin() + task.getSpentMin());
        }
        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);

    }

    public void done(CallbackQuery query, SendMessage sendMessage) {
        int messageId = query.getMessage().getMessageId();
        String callBackData = query.getData();

        Long taskId = Long.valueOf(callBackData.substring(callBackData.indexOf("_") + 1));
        Task task;

        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            task = taskOptional.get();
            task.setDone(true);
            if (task.getVolumeType().equals(VolumeType.COUNT)) {
                task.setSpentCount(task.getPlanCount());
            } else {
                task.setSpentMin(task.getPlanMin());
            }
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
        } else {
            return;
        }
        inMemoryRepository.removeMessage(messageId, sendMessage.getChatId());
        bot.deleteMessage(messageId, sendMessage.getChatId());
        sendMessage.setText("success 😊");
        sendMessage.enableNotification();
        bot.sendMessage(sendMessage);

    }

    public void sendMessage(SendMessage sendMessage) {
        bot.sendMessage(sendMessage);
    }

    public void deleteMessage(int messageId, String chatId) {
        bot.deleteMessage(messageId, chatId);
    }

    public void sendTasks(String key) {
        Optional<TelegramUser> userOptional = telegramUserRepository.findByKey(key);
        TelegramUser user;
        String chatId;

        if (userOptional.isPresent() && !userOptional.get().getTodaySent()) {
            user = userOptional.get();
            chatId = user.getChatId();
            inMemoryRepository.removeAllMessage(chatId);
            List<Task> tasks = taskRepository.findAllByUserId(user.getId());
            user.setTodaySent(true);
            telegramUserRepository.save(user);
            tasks.forEach(task -> {
                SendMessage sendMessage = new SendMessage();
                markupBoard.doneButton(sendMessage, task);
                sendMessage.setChatId(chatId);
                if (task.getVolumeType().equals(VolumeType.COUNT)) {
                    sendMessage.setText("<b>Vazifa: </b>" + task.getName() + "\n<b>" + VolumeType.COUNT.getNameAndName1() + ": </b>" + task.getPlanCount());
                } else {
                    sendMessage.setText("<b>Vazifa: </b>" + task.getName() + "(" + task.getPlanMin() + " min)");

                }
                inMemoryRepository.addMessId(chatId, bot.sendMessage(sendMessage));
            });
        }
    }

    public void saveUser(Message message) {
        String key = message.getText().substring(0, message.getText().indexOf("#"));
        Optional<TelegramUser> userOptional = telegramUserRepository.findByKey(key);
        TelegramUser user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new TelegramUser();
            user.setKey(key);
        }

        user.setChatId(message.getChatId().toString());
        user.setFullName(message.getFrom().getFirstName());
        user.setTodaySent(false);
        telegramUserRepository.save(user);

    }

    public Task addTask(Task task) {
        if (task.getPlanCount() == null)
            task.setPlanMin(0L);
        if (task.getSpentCount() == null)
            task.setSpentCount(0);
        if (task.getSpentMin() == null)
            task.setSpentMin(0L);
        if (task.getPlanCount() == null)
            task.setPlanCount(0);

        return taskRepository.save(task);
    }

    public void updateTaskVolume(Long taskId, VolumeType volumeType) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Task task;
        if (taskOptional.isPresent()) {
            task = taskOptional.get();
            task.setVolumeType(volumeType);
            taskRepository.save(task);
        } else {
            throw new BadRequestException("cant update task volume type");
        }
    }

    public TelegramUser getUser(String chatId) {
        return telegramUserRepository.findByChatId(chatId).orElseThrow();
    }
}
