package com.example.dailyrutine.button;

import com.example.dailyrutine.entity.Task;
import com.example.dailyrutine.enums.DailyType;
import com.example.dailyrutine.enums.VolumeType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Muhammadkomil Murodillayev, сб 19:42. 03/06/23
 */
@Component
public class MarkupBoard {

    public static final ReplyKeyboardMarkup board = new ReplyKeyboardMarkup();
    public static final InlineKeyboardMarkup keyBoard = new InlineKeyboardMarkup();


    public void mainMenu(SendMessage sendMessage) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("add task"));
        row.add(new KeyboardButton("edit task"));
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("new tasks"));
        row1.add(new KeyboardButton("statistic"));
        board.setKeyboard(List.of(row, row1));
        board.setResizeKeyboard(true);
        board.setSelective(true);
        sendMessage.setReplyMarkup(board);
    }

    public void dailyTypes(SendMessage sendMessage) {

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (DailyType value : DailyType.values()) {
            buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(value.getName())
                            .callbackData(value.name())
                            .build()
            ));
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(buttons);
        sendMessage.setReplyMarkup(keyboard);

    }

    public void volumeTypes(SendMessage sendMessage) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (VolumeType value : VolumeType.values()) {
            buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text((value.getName().equals("minut")) ? value.getName() : value.getNameAndName1())
                            .callbackData(value.name())
                            .build()
            ));
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(buttons);
        sendMessage.setReplyMarkup(keyboard);

    }


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