package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.*;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.util.InlineKeyboardMarkupUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "add", accessRight = UserRole.USER, maxButtonInRow = 2)
public class AddHandler extends Handler {

    private static final byte OPTION_BUTTON_IN_ROW_SIZE = 1;

    @Autowired
    private AddMediaHandler addMediaHandler;

    @Autowired
    private AddTextHandler addTextHandler;

    @Autowired
    private AddLocationHandler addLocationHandler;

    @Autowired
    private AddTimeHandler addTimeHandler;

    @Autowired
    private ConfirmAddHandler confirmAddHandler;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {

        List<List<InlineKeyboardButton>> buttons = InlineKeyboardMarkupUtil
                .getKeyboard(
                        InlineKeyboardMarkupUtil.convert(getAddOption(), user, update),
                        OPTION_BUTTON_IN_ROW_SIZE);

        buttons.addAll(InlineKeyboardMarkupUtil
                .getKeyboard(
                        InlineKeyboardMarkupUtil.convert(getMethods(), user, update),
                        getMaxButtonInRow()));

        return new InlineKeyboardMarkup().setKeyboard(buttons);
    }

    @Override
    public List<Handler> getChild() {
        List<Handler> handlers = new LinkedList<>();
        handlers.addAll(getAddOption());
        handlers.addAll(getMethods());
        return handlers;
    }

    private List<Handler> getAddOption() {
        return Arrays.asList(addMediaHandler, addTextHandler,
                addLocationHandler, addTimeHandler);
    }

    private List<Handler> getMethods() {
        return Arrays.asList(confirmAddHandler,
                backAndRejectIncidentHandler);
    }
}
