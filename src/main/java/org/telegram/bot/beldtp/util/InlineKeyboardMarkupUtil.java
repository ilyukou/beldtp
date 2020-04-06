package org.telegram.bot.beldtp.util;

import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class InlineKeyboardMarkupUtil {

    public static InlineKeyboardMarkup getMarkup(final List<InlineKeyboardButton> buttons,
                                                 final byte maxButtonInRow) {
        return new InlineKeyboardMarkup().setKeyboard(getKeyboard(buttons, maxButtonInRow));
    }

    public static List<List<InlineKeyboardButton>> getKeyboard(final List<InlineKeyboardButton> buttons,
                                                               final byte maxButtonInRow) {
        List<List<InlineKeyboardButton>> keyboard = new LinkedList<>();

        for (int i = 0; i < buttons.size(); i += maxButtonInRow) {
            List<InlineKeyboardButton> row = new LinkedList<>();

            // If remaining buttons size less than max size in one row -> add all in a row
            if (i + maxButtonInRow > buttons.size()) {
                for (int j = i; j < buttons.size(); j++) {
                    row.add(buttons.get(j));
                }
            } else {
                for (int j = i; j < i + maxButtonInRow; j++) {
                    row.add(buttons.get(j));
                }
            }

            keyboard.add(row);
        }

        return keyboard;
    }

    public static InlineKeyboardMarkup getMarkup(final List<InlineKeyboardButton> buttons,
                                                 final List<Handler> handlers, User user, Update update,
                                                 final byte maxButtonInRow) {

        List<List<InlineKeyboardButton>> keyboard = getKeyboard(buttons, maxButtonInRow);

        keyboard.addAll(getKeyboard(convert(handlers, user, update), maxButtonInRow));

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public static InlineKeyboardMarkup getMarkup(final List<Handler> handlers, User user, Update update,
                                                 final byte maxButtonInRow) {
        return getMarkup(convert(handlers, user, update), maxButtonInRow);
    }

    public static InlineKeyboardButton convert(Handler handler, User user, Update update) {
        return new InlineKeyboardButton()
                .setText(handler.getLabel(user, update))
                .setCallbackData(handler.getType());
    }

    public static List<InlineKeyboardButton> convert(List<Handler> handlers, User user, Update update) {
        return handlers
                .stream()
                .map(handler -> convert(handler, user, update))
                .collect(Collectors.toList());
    }
}
