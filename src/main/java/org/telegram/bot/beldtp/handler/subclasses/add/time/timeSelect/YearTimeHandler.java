package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.TimeService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.InlineKeyboardMarkupUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "timeYear", accessRight = UserRole.USER, maxButtonInRow = 5)
public class YearTimeHandler extends Handler {

    private static final Integer START_YEAR_FROM = 2000;

    @Autowired
    private MonthTimeHandler monthTimeHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeService timeService;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        List<InlineKeyboardButton> buttons = new LinkedList<>();

        for (int year = START_YEAR_FROM; year <= nowYear; year++) {
            buttons.add(new InlineKeyboardButton()
                    .setCallbackData(String.valueOf(year))
                    .setText(String.valueOf(year)));
        }

        return InlineKeyboardMarkupUtil.getMarkup(buttons, getChild(), user, update, getMaxButtonInRow());
    }

    @Override
    public List<TelegramResponse> handle( User user, Update update) {

        List<TelegramResponse> transition = transaction(user, update);

        if (transition != null) {
            return transition;
        }

        if (!isValid(update)) {
            throw new BadRequestException();
        }

        Incident incident = incidentService.getDraft(user);

        Time time = null;

        if (incident.hasTime()) {
            time = incident.getTime();
        } else {
            time = new Time();
        }

        time.setYear(Integer.parseInt(update.getCallbackQuery().getData()));

        time.setIncident(incident);
        time = timeService.save(time);

        incident.setTime(time);
        incident = incidentService.save(incident);

        user.pushStatus(monthTimeHandler.getType());
        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private boolean isValid(Update update) {

        if (!update.hasCallbackQuery()) {
            return false;
        }

        try {
            Integer year = Integer.parseInt(update.getCallbackQuery().getData());
            if (year < 0 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
