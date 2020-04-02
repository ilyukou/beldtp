package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

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

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "timeMonth", accessRight = UserRole.USER)
public class TimeMonthHandler extends Handler {

    private static List<String> MONTH_OF_YEAR = Arrays
            .asList("january", "february", "march",
                    "april", "may", "june",
                    "july", "august", "september",
                    "october", "november", "december");

    @Autowired
    private TimeDayHandler timeDayHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private AnswerService answerRamStorage;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private TimeYearHandler timeYearHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Incident draft = incidentService.getDraft(user);

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(user.getId());
            editMessageText.setReplyMarkup(getMonthOfYearButton(user.getLanguage(), draft.getTime().getYear()));
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText());

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setText(getAnswer(user.getLanguage()).getText());
        sendMessage.setReplyMarkup(getMonthOfYearButton(user.getLanguage(), draft.getTime().getYear()));

        return new TelegramResponse(sendMessage);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        TelegramResponse transition = transaction(user,update);

        if (transition != null) {
            return transition;
        }

        if (update.hasCallbackQuery()) {
            return handleCallbackQuery(user, update);
        }

        return null;
    }

    public TelegramResponse handleCallbackQuery(User user, Update update) {

        telegramResponseBlockingQueue.push(new TelegramResponse(
                new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
        ));

        if (update.getCallbackQuery().getData().equals(backHandler.getAnswer(user.getLanguage()).getType())) {

            user.popStatus();
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user,update);
        }
//        if(update.getCallbackQuery().getData().equals(backLogicComponent.getAnswer(user.getLanguage()).getType())){
//
//            user.popStatus();
//            user = userService.save(user);
//
//            return super.getByUserStatus(user.getStatus()).getComponentMessage(user,update);
//        }

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

//        user.popStatus();
        user.pushStatus(timeDayHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private Byte getMonthIdFromUpdate(String data) {

        for (int i = 0; i < MONTH_OF_YEAR.size(); i++) {
            if (data.equals(MONTH_OF_YEAR.get(i))) {
                return (byte) i;
            }
        }

        return null;
    }


    private InlineKeyboardMarkup getMonthOfYearButton(Language language, int year) {
        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.YEAR) < year){
            // FIXME incorrect YEAR
            throw new IllegalArgumentException("Incorrect year. Now: "
                    + calendar.get(Calendar.YEAR) + ", request: " + year);
        }

        List<List<InlineKeyboardButton>> result = new LinkedList<>();

        if(calendar.get(Calendar.YEAR) > year ){ // request past year

            int countOfMonthInRow = 3;
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (String month : MONTH_OF_YEAR) {
                row.add(
                        new InlineKeyboardButton()
                                .setText(answerRamStorage.get(month, language).getLabel())
                                .setCallbackData(month)
                );
                if (row.size() == countOfMonthInRow) {
                    result.add(row);
                    row = new LinkedList<>();
                }
            }

        } else { // request currently year

            int monthNowIndex = calendar.get(Calendar.MONTH) + 1;

            for (int i = 0; i < monthNowIndex; i++) {
                result.add(Arrays.asList(
                        new InlineKeyboardButton()
                                .setText(answerRamStorage.get(MONTH_OF_YEAR.get(i), language).getLabel())
                                .setCallbackData(MONTH_OF_YEAR.get(i)))
                );
            }
        }

        result.add(
                Arrays.asList(new InlineKeyboardButton()
                        .setText(backHandler.getAnswer(language).getLabel())
                        .setCallbackData(backHandler.getAnswer(language).getType()))
        );

        return new InlineKeyboardMarkup().setKeyboard(result);
    }
}
