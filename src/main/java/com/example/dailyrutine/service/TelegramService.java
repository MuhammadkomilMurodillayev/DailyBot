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
import com.example.dailyrutine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

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
    private final UserRepository userRepository;
    private final InMemoryRepository inMemoryRepository;
    private final MarkupBoard markupBoard;
    private final Bot bot;

    //    @Scheduled(cron = "0 0 0 * * *")
    private void sendTasks() {
        List<TelegramUser> users = userRepository.findAll();
        users.forEach(user -> {
            inMemoryRepository.removeAllMessage(user.getChatId());
            sendTasks(user.getKey());
        });
    }

    public void done(CallbackQuery query, SendMessage sendMessage) {
        int messageId = query.getMessage().getMessageId();
        String callBackData = query.getData();

        Long taskId = Long.valueOf(callBackData.substring(callBackData.indexOf("_") + 1));
        History history = null;
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

        Optional<History> historyOptional = historyRepository.findByTaskId(taskId);

        history = historyOptional.orElseGet(History::new);

        if (historyOptional.isPresent()){
            history = historyOptional.get();
        }else {
            history.setTaskId(taskId);
            history.setTotalPlanCount(0L);
            history.setTotalPlanMin(0L);
            history.setTotalSpentCount(0L);
            history.setTotalSpentMin(0L);
            history = historyRepository.save(history);
        }


        if (task.getVolumeType().equals(VolumeType.COUNT)) {
            history.setTotalPlanCount(history.getTotalPlanCount() + task.getPlanCount());
            history.setTotalSpentCount(history.getTotalSpentCount() + task.getSpentCount());
        } else {
            history.setTotalPlanMin(history.getTotalPlanMin() + task.getPlanMin());
            history.setTotalSpentMin(history.getTotalSpentMin() + task.getSpentMin());
        }

        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);
        inMemoryRepository.removeMessage(messageId, sendMessage.getChatId());
        bot.deleteMessage(messageId, sendMessage.getChatId());
        sendMessage.setText("success 😊");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        bot.sendMessage(sendMessage);

    }

    public void sendMessage(SendMessage sendMessage) {
        bot.sendMessage(sendMessage);
    }

    public void deleteMessage(int messageId, String chatId) {
        bot.deleteMessage(messageId, chatId);
    }

    public void sendTasks(String key) {
        Optional<TelegramUser> userOptional = userRepository.findByKey(key);
        TelegramUser user;
        String chatId;

        if (userOptional.isPresent() && !userOptional.get().getTodaySent()) {
            user = userOptional.get();
            chatId = user.getChatId();
            inMemoryRepository.removeAllMessage(chatId);
            List<Task> tasks = taskRepository.findAllByUserId(user.getId());
            user.setTodaySent(true);
            userRepository.save(user);
            tasks.forEach(task -> {
                SendMessage sendMessage = new SendMessage();
                markupBoard.doneButton(sendMessage, task);
                sendMessage.setChatId(chatId);
                if (task.getVolumeType().equals(VolumeType.COUNT)) {
                    sendMessage.setText(task.getPlanCount() + " " + task.getName());
                } else {
                    sendMessage.setText(task.getName() + "(" + task.getPlanMin() + " min)");

                }
                inMemoryRepository.addMessId(chatId, bot.sendMessage(sendMessage));
            });
        }
    }

    public void saveUser(Message message) {
        String key = message.getText().substring(0, message.getText().indexOf("#"));
        Optional<TelegramUser> userOptional = userRepository.findByKey(key);
        TelegramUser user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new TelegramUser();
            user.setKey(key);
        }

        user.setChatId(message.getChatId().toString());
        user.setFullName(message.getFrom().getFirstName());
        userRepository.save(user);

    }


}
