package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
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

@HandlerInfo(type = "timeYear", accessRight = UserRole.USER)
public class TimeYearHandler extends Handler {

    private static final int COUNT_OF_BUTTON_WITH_YEAR = 9;

    @Autowired
    private TimeMonthHandler timeMonthHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private BackHandler backHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        if(update.hasCallbackQuery()){
            EditMessageText editMessageReplyMarkup = new EditMessageText();
            editMessageReplyMarkup.setChatId(user.getId());
            editMessageReplyMarkup.setReplyMarkup(getYearButton(user.getLanguage()));
            editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageReplyMarkup.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageReplyMarkup.setText(getAnswer(user.getLanguage()).getText());

            return new TelegramResponse(editMessageReplyMarkup,update);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setReplyMarkup(getYearButton(user.getLanguage()));
        sendMessage.setText(getAnswer(user.getLanguage()).getText());

        return new TelegramResponse(sendMessage);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        if (update.hasCallbackQuery()) {
            return handleCallbackQuery(user,update);

        }
        return null;
    }


    public TelegramResponse handleCallbackQuery(User user, Update update) {
        TelegramResponse transition = transaction(user,update);

        if(transition != null){
            return transition;
        }

        telegramResponseBlockingQueue.push(new TelegramResponse(
                new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
        ));

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

        user.pushStatus(timeMonthHandler.getType());
        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    public InlineKeyboardMarkup getYearButton(Language language) {
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        List<List<InlineKeyboardButton>> result = new LinkedList<>();

        int step = 3;

        for (int i = 0; i < COUNT_OF_BUTTON_WITH_YEAR; i += step) {
            List<InlineKeyboardButton> buttons = new LinkedList<>();

            for (int j = i; j < i + step; j++) {
                buttons.add(new InlineKeyboardButton()
                        .setCallbackData(String.valueOf(nowYear - j))
                        .setText(String.valueOf(nowYear - j)));
            }
            result.add(buttons);
        }

        result.add(Arrays.asList(
                new InlineKeyboardButton()
                    .setCallbackData(backHandler.getType())
                    .setText(backHandler.getAnswer(language).getLabel())));

        return new InlineKeyboardMarkup().setKeyboard(result);
    }
}
