package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.TimeService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


@HandlerInfo(type = "timeDay", accessRight = UserRole.USER)
public class TimeDayHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDayHandler.class);

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
    private TimeHourHandler timeHourHandler;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Incident incident = incidentService.getDraft(user);

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(user.getId());
            editMessageText
                    .setReplyMarkup(
                            getDayOfMonth(
                                    incident.getTime().getMonth(),
                                    incident.getTime().getYear(), user.getLanguage()));
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText()); // "Pick day"

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage
                .setReplyMarkup(
                        getDayOfMonth(
                                incident.getTime().getMonth(),
                                incident.getTime().getYear(),
                                user.getLanguage()));
        sendMessage.setText(getAnswer(user.getLanguage()).getText()); // "Pick day"
        return new TelegramResponse(sendMessage);


    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        if (update.hasMessage()) {
            return handleMessage(user,update);

        } else if (update.hasCallbackQuery()) {
            return handleCallbackQuery(user,update);

        } else {
            return null;
        }
    }

    public TelegramResponse handleMessage(User user, Update update) {
        TelegramResponse transition = transaction(user,update);

        return transition;
    }

    public TelegramResponse handleCallbackQuery(User user,Update update) {

        telegramResponseBlockingQueue.push(new TelegramResponse(
                new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
        ));


//        if(update.getCallbackQuery().getData().equals(backLogicComponent.getAnswer(user.getLanguage()).getType())){
//
//            user.popStatus();
//            user = userService.save(user);
//
//            TelegramResponse telegramResponse = super.getByUserStatus(user.getStatus()).getComponentMessage(user,update);
//            EditMessageReplyMarkup editMessageReplyMarkup = telegramResponse.getEditMessageReplyMarkup();
//            editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//            editMessageReplyMarkup.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
//
//            telegramResponse.setEditMessageReplyMarkup(editMessageReplyMarkup);
//
//            return telegramResponse;
//        }
        if (update.getCallbackQuery().getData().equals(backHandler.getAnswer(user.getLanguage()).getType())) {

            user.popStatus();
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
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

        user.pushStatus(timeHourHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private InlineKeyboardMarkup getDayOfMonth(int monthId, int year, Language language) {

        List<List<InlineKeyboardButton>> month = new LinkedList<>();

        Calendar calendar = Calendar.getInstance();

        int days = 0;
        int daysInRow = 4;

        if(calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == monthId ){
            days = calendar.get(Calendar.DATE);

        } else {
            days = getCountOfMonth(monthId, year);
        }

        List<InlineKeyboardButton> row = new LinkedList<>();

        for (int i = 0; i < days; i++) {
            row.add(
//                    new Button(String.valueOf(i+1), String.valueOf(i+1))
                    getButtonForDay(monthId, year, i + 1, language)
            );
            if (row.size() == daysInRow) {
                month.add(row);
                row = new LinkedList<>();

                if (i + 1 == days) {
                    break;
                }
            }

            if (i + 1 == days) {
                month.add(row);
                row = new LinkedList<>();
            }
        }

        month.add(
                Arrays.asList(new InlineKeyboardButton()
                        .setText(backHandler.getAnswer(language).getLabel())
                                .setCallbackData(backHandler.getAnswer(language).getType()))
        );

        return new InlineKeyboardMarkup().setKeyboard(month);
    }

    private InlineKeyboardButton getButtonForDay(int monthId, int year, int day, Language language) {
//        LOGGER.info(day+"/"+monthId+"/"+year);
        // month from 0 to max. day from 0 to max
        LocalDate localDate = LocalDate.of(year, monthId + 1, day);

        return new InlineKeyboardButton()
                .setText(day + " - " + getShortDayNameOfWeek(DayOfWeek.from(localDate), language))
                .setCallbackData(String.valueOf(day));
    }

    private String getShortDayNameOfWeek(DayOfWeek dayOfWeek, Language language) {

        Answer answer = answerRamStorage.get(dayOfWeek.toString().toLowerCase(), language);

        return answer != null ? answer.getLabel() : dayOfWeek.toString().toLowerCase();
    }


    public int getCountOfMonth(int month, int year) {

        switch (month) {
            case 0:
                return 31;

            case 1:
                if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))) {
                    return 29;
                } else {
                    return 28;
                }

            case 2:
                return 31;

            case 3:
                return 30;

            case 4:
                return 31;

            case 5:
                return 30;

            case 6:
                return 31;

            case 7:
                return 31;

            case 8:
                return 30;

            case 9:
                return 31;

            case 10:
                return 30;

            case 11:
                return 31;

            default:
                // FIXME
                return 30;
        }
    }
}
