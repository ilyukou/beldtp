package org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "timeHour", accessRight = UserRole.USER)
public class TimeHourHandler extends Handler {

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private TimeMinuteHandler timeMinuteHandler;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Incident draft = incidentService.getDraft(user);

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(user.getId());
            editMessageText.setReplyMarkup(getHourButton(user.getLanguage(), draft.getTime()));
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText());

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setReplyMarkup(getHourButton(user.getLanguage(), draft.getTime()));
        sendMessage.setText(getAnswer(user.getLanguage()).getText());

        return new TelegramResponse(sendMessage);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {

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

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        Incident draft = incidentService.getDraft(user);

        Time time = draft.getTime();

        time.setHour(getByteFromTimeString(update.getCallbackQuery().getData()));

        draft.setTime(time);

        draft = incidentService.save(draft);

        user.pushStatus(timeMinuteHandler.getType());

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private Byte getByteFromTimeString(String string) {
        return Byte.parseByte(string.substring(1)); // remove 'h' from string. Example h35 -> 35
    }

    private InlineKeyboardMarkup getHourButton(Language language, Time time) {

        int COUNT_BUTTON_IN_ONE_ROW = 3;

        List<List<InlineKeyboardButton>> result = new LinkedList<>();

        List<InlineKeyboardButton> row = new LinkedList<>();

        Calendar calendar = Calendar.getInstance();
        int size = 24;

        if (calendar.get(Calendar.YEAR) == time.getYear()
                && calendar.get(Calendar.MONTH) == time.getMonth()
                && calendar.get(Calendar.DATE) == time.getDay()) {
           size = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        }

        if(size <= COUNT_BUTTON_IN_ONE_ROW){
            // Если размер часов меньше чем возмодности одной строчки кнопок
            for (int t=0; t < size; t++){
                row.add(new InlineKeyboardButton().setText(String.valueOf(t)).setCallbackData( "h" + t));
            }
            result.add(row);
            row = new LinkedList<>();

        }else {
            // если размер больше чем возможности одной строчки кнопок
            int i = 0;
            // вначале добавляю и проверяю, есть ли еще возможность полностью заполнить слой
            do {
                for (int t = i; t < i + COUNT_BUTTON_IN_ONE_ROW; t++) {
                    row.add(new InlineKeyboardButton().setText(String.valueOf(t)).setCallbackData( "h" + t));
                }
                result.add(row);
                row = new LinkedList<>();

                i += COUNT_BUTTON_IN_ONE_ROW;
            } while (size > i + COUNT_BUTTON_IN_ONE_ROW);

            // напоследок проверяю, вдруг кроме моих заполненны слоев остались еще кнопки.
            // они точно будут меньше одного ряда и я их добавляю сразу все
            if(size > result.size() * COUNT_BUTTON_IN_ONE_ROW){
                int diff = size - result.size() * COUNT_BUTTON_IN_ONE_ROW;

                for (int t = size - diff; t < size; t++) {
                    row.add(new InlineKeyboardButton().setText(String.valueOf(t)).setCallbackData( "h" + t));
                }
                result.add(row);
            }
        }

        // FIXME - add idk button
        result.add(
                Arrays.asList(new InlineKeyboardButton()
                        .setText(backHandler.getAnswer(language).getLabel())
                        .setCallbackData(backHandler.getAnswer(language).getType()))
        );

        return new InlineKeyboardMarkup(result);
    }
}
