package com.example.dailyrutine.repository;

import com.example.dailyrutine.Bot;
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

    public final Map<String, Set<Integer>> TEMPORARY_MESSAGES = new HashMap<>();
    private final Bot bot;
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
            TEMPORARY_MESSAGES.forEach((k, v) -> {
                if (v.toString().equals(chatId)) {
                    TEMPORARY_MESSAGES.get(chatId).forEach(m -> {
                        bot.deleteMessage(m, chatId);
                    });
                }
            });
        }
    }

    public void removeMessage(int messageId, String chatId) {
        if (!TEMPORARY_MESSAGES.isEmpty()) {
            TEMPORARY_MESSAGES.forEach((k, v) -> {
                if (v.toString().equals(chatId)) {
                    TEMPORARY_MESSAGES.get(chatId).forEach(m -> {
                        if (messageId == m) {
                            bot.deleteMessage(messageId, chatId);
                        }
                    });
                }
            });
        }
    }


}
