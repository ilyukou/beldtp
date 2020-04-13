package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.AddHandler;
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

@HandlerInfo(type = "timeMinute", accessRight = UserRole.USER, maxButtonInRow = 5)
public class MinuteTimeHandler extends Handler {

    @Autowired
    private BackHandler backLogicComponent;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddHandler addHandler;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        Incident incident = incidentService.getDraft(user);
        return getMinuteButton(user, update, incident.getTime());
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

        time.setMinute(Byte.parseByte(update.getCallbackQuery().getData()));

        draft.setTime(time);

        draft = incidentService.save(draft);

        while (!user.peekStatus().equals(addHandler.getType())) {
            user.popStatus();
        }

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private boolean isValid(Update update) {

        if (!update.hasCallbackQuery()) {
            return false;
        }

        try {
            byte minute = Byte.parseByte(update.getCallbackQuery().getData());
            if (minute < 0 || minute > 59) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private InlineKeyboardMarkup getMinuteButton(User user, Update update, Time time) {

        String hourString = String.valueOf(time.getHour());

        if (time.getHour() < 10) {
            hourString = "0" + hourString;
        }

        int minute = 59;

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == time.getYear()
                && calendar.get(Calendar.MONTH) == time.getMonth()
                && calendar.get(Calendar.DATE) == time.getDay()
                && calendar.get(Calendar.HOUR_OF_DAY) == time.getHour()){
            minute = calendar.get(Calendar.MINUTE);
        }

        List<InlineKeyboardButton> buttons = new LinkedList<>();

        for (int t = 0; t <= minute; t++) {
            if (t < 10) {
                buttons.add(new InlineKeyboardButton()
                        .setText(hourString + ":" + "0" + t)
                        .setCallbackData(String.valueOf(t)));

            } else {
                buttons.add(new InlineKeyboardButton()
                        .setText(hourString + ":" + t)
                        .setCallbackData(String.valueOf(t)));
            }
        }

        return InlineKeyboardMarkupUtil.getMarkup(buttons, getChild(), user, update, getMaxButtonInRow());
    }
}
