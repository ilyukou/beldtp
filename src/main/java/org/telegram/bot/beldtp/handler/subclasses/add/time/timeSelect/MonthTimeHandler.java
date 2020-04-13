package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.TimeService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.InlineKeyboardMarkupUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "timeMonth", accessRight = UserRole.USER, maxButtonInRow = 3)
public class MonthTimeHandler extends Handler {

    private static List<String> MONTH_OF_YEAR = Arrays
            .asList("january", "february", "march",
                    "april", "may", "june",
                    "july", "august", "september",
                    "october", "november", "december");

    @Autowired
    private DayTimeHandler dayTimeHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private AnswerService answerRamStorage;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private YearTimeHandler yearTimeHandler;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        Incident draft = incidentService.getDraft(user);
        return getMonthOfYearButton(user, update, draft.getTime().getYear());
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

        Byte month = getMonthIdFromUpdate(update.getCallbackQuery().getData());

        if (month == null) {
            return null;
        }

        time.setMonth(month);

        time.setIncident(incident);
        time = timeService.save(time);

        incident.setTime(time);
        incident = incidentService.save(incident);

        user.pushStatus(dayTimeHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private boolean isValid(Update update) {

        if (!update.hasCallbackQuery()) {
            return false;
        }

        try {
            byte hour = getMonthIdFromUpdate(update.getCallbackQuery().getData());

            if (hour < 0 || hour > 11) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Byte getMonthIdFromUpdate(String data) {

        for (int i = 0; i < MONTH_OF_YEAR.size(); i++) {
            if (data.equals(MONTH_OF_YEAR.get(i))) {
                return (byte) i;
            }
        }

        throw new BadRequestException();
    }


    private InlineKeyboardMarkup getMonthOfYearButton(User user, Update update, int year) {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) < year) {
            // FIXME incorrect YEAR
            throw new IllegalArgumentException("Incorrect year. Now: "
                    + calendar.get(Calendar.YEAR) + ", request: " + year);
        }

        int month = 12;

        if (calendar.get(Calendar.YEAR) == year) { // request past year
            month = calendar.get(Calendar.MONTH) + 1;

        }

        List<InlineKeyboardButton> buttons = new LinkedList<>();

        for (int i = 0; i < month; i++) {
            buttons.add(new InlineKeyboardButton()
                    .setText(answerRamStorage.get(MONTH_OF_YEAR.get(i), user.getLanguage()).getText())
                    .setCallbackData(MONTH_OF_YEAR.get(i)));
        }

        return InlineKeyboardMarkupUtil.getMarkup(buttons, getChild(), user, update, getMaxButtonInRow());
    }
}
