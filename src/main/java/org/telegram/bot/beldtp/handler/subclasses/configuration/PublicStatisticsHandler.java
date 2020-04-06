package org.telegram.bot.beldtp.handler.subclasses.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;

@HandlerInfo(type = "publicStatistics", accessRight = UserRole.ADMIN)
public class PublicStatisticsHandler extends Handler {

    private static final String COUNT_OF_USER = "countOfUser";
    private static final String WHAT_LANGUAGE_ARE_USED = "whatLanguageAreUsed";
    private static final String COUNT_OF_INCIDENT = "countOfIncident";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        if (update.hasCallbackQuery()) {
            EditMessageText editMessageText = new EditMessageText();

            editMessageText.setChatId(user.getId());

            editMessageText.setText(getStatistics(user.getLanguage(), Calendar.getInstance()));

            editMessageText.setReplyMarkup(getInlineKeyboardMarkup(user, update));

            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            telegramResponseBlockingQueue.push(
                    new TelegramResponse(new AnswerCallbackQuery()
                            .setCallbackQueryId(update.getCallbackQuery().getId())));

            return new TelegramResponse(editMessageText,update);
        }

        return new TelegramResponse(
                new SendMessage()
                        .setText(getStatistics(user.getLanguage(), Calendar.getInstance()))
                        .setChatId(user.getId())
                        .setReplyMarkup(getInlineKeyboardMarkup(user, update)));
    }

    private String getStatistics(Language language, Calendar calendarInstance){
        Language[] languages = Language.values();
        StringBuilder languageStat = new StringBuilder();
        double countUser = userService.size();

        for (Language lan : languages){
            long count = userService.size(lan);
            languageStat
                    .append(lan.getValue())
                    .append(" : ")
                    .append(count)
                    .append(" ( ").append(count/countUser*100).append(" % )")
                    .append("\n");
        }

        StringBuilder stringBuilder = new StringBuilder()
                .append(getAnswer(language).getText()).append("\n")
                .append("\n")
                .append(answerService.get(COUNT_OF_USER, language)).append(" ").append(userService.size()).append("\n")
                .append("\n")
                .append(answerService.get(WHAT_LANGUAGE_ARE_USED, language)).append("\n")
                .append("\n")
                .append(languageStat.toString())
                .append("\n")
                .append(answerService.get(COUNT_OF_INCIDENT, language)).append(" ")
                .append(incidentService.size(IncidentType.PUBLISH)).append("\n")
                .append("\n")
//                .append("Count of message from user to bot : ").append(.count()).append("\n") FIXME - add function
                .append("\n")
                .append(calendarInstance.get(Calendar.HOUR_OF_DAY)).append(":");

        if(calendarInstance.get(Calendar.MINUTE) < 10){
            stringBuilder.append("0" + calendarInstance.get(Calendar.MINUTE));
        }else {
            stringBuilder.append(calendarInstance.get(Calendar.MINUTE));
        }

        stringBuilder
                .append(" ")
                .append(calendarInstance.get(Calendar.DATE))
                .append("/")
                .append(calendarInstance.get(Calendar.MONTH) + 1)
                .append("/")
                .append(calendarInstance.get(Calendar.YEAR));

        return stringBuilder.toString();
    }
}