package com.example.dailyrutine.repository;

import com.example.dailyrutine.Bot;
import com.example.dailyrutine.entity.Task;
import com.example.dailyrutine.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Muhammadkomil Murodillayev, пн 01:15. 05/06/23
 */
@Repository
@RequiredArgsConstructor
public class InMemoryRepository {

    //TEMPORARY MESSAGE key is chatId , Set<Integer> is messageId list
    public final Map<String, Set<Integer>> TEMPORARY_MESSAGES = new HashMap<>();
    public final Map<String, Task> CREATE_TASKS = new HashMap<>();
    private final Bot bot;
    public Map<String, State> STATES = new HashMap<>();

    public void addMessId(String chatId, int messageId) {

        if (TEMPORARY_MESSAGES.get(chatId) == null) {
            TEMPORARY_MESSAGES.put(chatId, Set.of(messageId));
        } else {
            Set<Integer> messages = TEMPORARY_MESSAGES.get(chatId);
            messages.add(messageId);
        }

    }

    public void removeAllMessage(String chatId) {
        if (!TEMPORARY_MESSAGES.isEmpty()) {
            TEMPORARY_MESSAGES.get(chatId).forEach(m -> bot.deleteMessage(m, chatId));
        }
    }

    public void removeMessage(int messageId, String chatId) {

        if (!TEMPORARY_MESSAGES.isEmpty()) {
            TEMPORARY_MESSAGES.get(chatId).forEach(m -> {
                if (messageId == m) bot.deleteMessage(messageId, chatId);
            });
        }
    }

    public void setState(State state, String chatId) {
        STATES.put(chatId, state);
    }

    public State getState(String chatId) {
        return (STATES.get(chatId) == null) ? State.ANONYMOUS : STATES.get(chatId);
    }

    public void setCreateTask(Task task, String chatId) {
        CREATE_TASKS.put(chatId, task);
    }

    public void removeCreateTask(String chatId) {
        CREATE_TASKS.remove(chatId);
    }

    public Task getCreateTask(String chatId) {

        if (CREATE_TASKS.get(chatId) == null)
            CREATE_TASKS.put(chatId, new Task());

        return CREATE_TASKS.get(chatId);
    }
}
