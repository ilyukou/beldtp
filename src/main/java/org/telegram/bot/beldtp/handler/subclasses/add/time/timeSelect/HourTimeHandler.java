package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.InlineKeyboardMarkupUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "timeHour", accessRight = UserRole.USER, maxButtonInRow = 6)
public class HourTimeHandler extends Handler {

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private MinuteTimeHandler minuteTimeHandler;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        return getHourButton(user, update, draft.getTime());
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {

        List<TelegramResponse> transition = transaction(user, update);

        if (transition != null) {
            return transition;
        }

        if (!isValid(update)) {
            throw new BadRequestException();
        }

        Incident draft = incidentService.getDraft(user);

        Time time = draft.getTime();

        time.setHour(Byte.parseByte(update.getCallbackQuery().getData()));

        draft.setTime(time);

        draft = incidentService.save(draft);

        user.pushStatus(minuteTimeHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private boolean isValid(Update update) {

        if (!update.hasCallbackQuery()) {
            return false;
        }

        try {
            byte hour = Byte.parseByte(update.getCallbackQuery().getData());
            if (hour < 0 || hour > 24) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private InlineKeyboardMarkup getHourButton(User user, Update update, Time time) {

        Calendar calendar = Calendar.getInstance();
        int size = 24;

        if (calendar.get(Calendar.YEAR) == time.getYear()
                && calendar.get(Calendar.MONTH) == time.getMonth()
                && calendar.get(Calendar.DATE) == time.getDay()) {
            size = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        }

        List<InlineKeyboardButton> buttons = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            buttons.add(new InlineKeyboardButton()
                    .setText(String.valueOf(i))
                    .setCallbackData(String.valueOf(i)));
        }

        return InlineKeyboardMarkupUtil.getMarkup(buttons, getChild(), user, update, getMaxButtonInRow());
    }
}
