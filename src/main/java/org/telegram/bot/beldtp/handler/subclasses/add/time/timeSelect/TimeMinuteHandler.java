package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.AddTimeHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@HandlerInfo(type = "timeMinute", accessRight = UserRole.USER)
public class TimeMinuteHandler extends Handler {

    @Autowired
    private BackHandler backLogicComponent;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private AddTimeHandler addTimeHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Incident incident = incidentService.getDraft(user);

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(user.getId());
            editMessageText.setReplyMarkup(getMinuteButton(user.getLanguage(), incident.getTime()));
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText());

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setReplyMarkup(getMinuteButton(user.getLanguage(), incident.getTime()));
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

        if (update.getCallbackQuery().getData().equals(backLogicComponent.getAnswer(user.getLanguage()).getType())) {

            user.popStatus();
            user = userService.save(user);

            telegramResponseBlockingQueue.push(new TelegramResponse(
                    new AnswerCallbackQuery()
                            .setCallbackQueryId(update.getCallbackQuery().getId())
            ));

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        Incident draft = incidentService.getDraft(user);

        Time time = draft.getTime();

        time.setMinute(getByteFromTimeString(update.getCallbackQuery().getData()));

        draft.setTime(time);

        draft = incidentService.save(draft);

        while (!user.peekStatus().equals(addTimeHandler.getType())) {
            user.popStatus();
        }

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private Byte getByteFromTimeString(String string) {
        return Byte.parseByte(string.substring(1)); // remove 'h' from string. Example h35 -> 35
    }

    private InlineKeyboardMarkup getMinuteButton(Language language, Time time) {

        int COUNT_BUTTON_IN_ONE_ROW = 4;

        List<List<InlineKeyboardButton>> result = new LinkedList<>();

        List<InlineKeyboardButton> row = new LinkedList<>();

        String hourString = String.valueOf(time.getHour());

        if (time.getHour() < 10) {
            hourString = "0" + hourString;
        }

        int minute = 60;

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) == time.getYear()
                && calendar.get(Calendar.MONTH) == time.getMonth()
                && calendar.get(Calendar.DATE) == time.getDay()
                && calendar.get(Calendar.HOUR_OF_DAY) == time.getHour()){
            minute = calendar.get(Calendar.MINUTE);
        }

        for (int t = 0; t < minute; t += 1) { // 36

            if (t < 10) {
                row.add(new InlineKeyboardButton()
                        .setText(hourString + ":" + "0" + t)
                        .setCallbackData("m" + t));

            } else {
                row.add(new InlineKeyboardButton()
                        .setText(hourString + ":" + t)
                        .setCallbackData("m" + t));
            }

            if (row.size() == COUNT_BUTTON_IN_ONE_ROW) {
                result.add(row);
                row = new LinkedList<>();
            }
        }
        // добавляю оставщийся незаполненный ряд в результат
        if(row.size() > 0){
            result.addAll(Collections.singletonList(row));
        }
        // FIXME - add idk button

        result.add(
                Arrays.asList(new InlineKeyboardButton()
                        .setText(backLogicComponent.getAnswer(language).getLabel()).
                                setCallbackData(backLogicComponent.getAnswer(language).getType()))
        );

        return new InlineKeyboardMarkup().setKeyboard(result);
    }
}
