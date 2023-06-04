package com.example.dailyrutine.button;

import com.example.dailyrutine.entity.Task;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Muhammadkomil Murodillayev, сб 19:42. 03/06/23
 */
@Component
public class MarkupBoard {

    public static final ReplyKeyboardMarkup board = new ReplyKeyboardMarkup();
    public static final InlineKeyboardMarkup keyBoard = new InlineKeyboardMarkup();


    public void doneButton(SendMessage sendMessage, Task task) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(
                InlineKeyboardButton.builder().text("done").callbackData("done_" + task.getId()).build(),
                InlineKeyboardButton.builder().text("unfinished").callbackData("unfinished_" + task.getId()).build()
        ));

        keyBoard.setKeyboard(buttons);
        sendMessage.setReplyMarkup(keyBoard);

    }

}