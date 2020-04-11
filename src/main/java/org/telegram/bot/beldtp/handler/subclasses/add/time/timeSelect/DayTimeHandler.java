package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * In this handler user pick the day
 */
@HandlerInfo(type = "timeDay", accessRight = UserRole.USER, maxButtonInRow = 4)
public class DayTimeHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DayTimeHandler.class);

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private AnswerService answerRamStorage;

    @Autowired
    private HourTimeHandler hourTimeHandler;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        Incident incident = incidentService.getDraft(user);

        return getDayOfMonth(
                incident.getTime().getMonth(),
                incident.getTime().getYear(), user.getLanguage());
    }

    @Override
    public List<TelegramResponse> handle(List<TelegramResponse> responses, User user, Update update) {
        List<TelegramResponse> transition = transaction(responses, user, update);

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

        time.setDay(Byte.parseByte(update.getCallbackQuery().getData()));

        time.setIncident(incident);
        time = timeService.save(time);

        incident.setTime(time);
        incident = incidentService.save(incident);

        user.pushStatus(hourTimeHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(responses, user, update);
    }

    private boolean isValid(Update update) {

        if (!update.hasCallbackQuery()) {
            return false;
        }

        try {
            byte day = Byte.parseByte(update.getCallbackQuery().getData());
            if (day < 0 || day > 31) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    private InlineKeyboardMarkup getDayOfMonth(int monthId, int year, Language language) {

        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();

        Calendar calendar = Calendar.getInstance();

        int days = 0;

        if (calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == monthId ){
            days = calendar.get(Calendar.DATE);

        } else {
            days = getCountDayInMonth(monthId, year);
        }

        List<InlineKeyboardButton> row = new LinkedList<>();

        for (int i = 0; i < days; i++) {
            row.add(
                    getButtonForDay(monthId, year, i + 1, language)
            );
            if (row.size() == getMaxButtonInRow()) {
                buttons.add(row);
                row = new LinkedList<>();

                if (i + 1 == days) {
                    break;
                }
            }

            if (i + 1 == days) {
                buttons.add(row);
                row = new LinkedList<>();
            }
        }

        buttons.add(
                Arrays.asList(new InlineKeyboardButton()
                        .setText(backHandler.getAnswer(language).getLabel())
                        .setCallbackData(backHandler.getAnswer(language).getType()))
        );

        return new InlineKeyboardMarkup().setKeyboard(buttons);
    }


    private InlineKeyboardButton getButtonForDay(int monthId, int year, int day, Language language) {

        LocalDate localDate = LocalDate.of(year, monthId + 1, day);

        return new InlineKeyboardButton()
                .setText(day + " - " + getShortDayNameOfWeek(DayOfWeek.from(localDate), language))
                .setCallbackData(String.valueOf(day));
    }

    private String getShortDayNameOfWeek(DayOfWeek dayOfWeek, Language language) {

        Answer answer = answerRamStorage.get(dayOfWeek.toString().toLowerCase(), language);

        return answer != null ? answer.getLabel() : dayOfWeek.toString().toLowerCase();
    }

    public int getCountDayInMonth(int month, int year) {
        switch (month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;

            case 1:
                if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))) {
                    return 29;
                } else {
                    return 28;
                }

            case 3:
            case 5:
            case 8:
            case 10:
                return 30;

            default:
                // FIXME
                return 30;
        }
    }
}
