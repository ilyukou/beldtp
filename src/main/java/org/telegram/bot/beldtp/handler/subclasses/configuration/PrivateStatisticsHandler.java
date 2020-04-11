package org.telegram.bot.beldtp.handler.subclasses.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;

@HandlerInfo(type = "privateStatistics", accessRight = UserRole.ADMIN)
public class PrivateStatisticsHandler extends Handler {

    private static final String USERS = "users";
    private static final String MODERATORS = "moderators";
    private static final String ADMINS = "admins";
    private static final String INCIDENTS = "incidents";
    private static final String PUBLISH_COUNT = "publishCount";
    private static final String REJECT_COUNT = "rejectCount";
    private static final String DELETE_COUNT = "deleteCount";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AnswerService answerService;

    @Override
    public String getText(User user, Update update) {
        return getStatistics(user.getLanguage(), Calendar.getInstance());
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
                .append(answerService.get(USERS, language).getText()).append(" ")
                .append(userService.size(UserRole.USER)).append("\n")
                .append(answerService.get(MODERATORS, language).getText()).append(" ")
                .append(userService.size(UserRole.MODERATOR)).append("\n")
                .append(answerService.get(ADMINS, language).getText()).append(" ")
                .append(userService.size(UserRole.ADMIN)).append("\n")
                .append(answerService.get(INCIDENTS, language).getText()).append("\n")
                .append(answerService.get(PUBLISH_COUNT, language).getText()).append(" ")
                .append(incidentService.size(IncidentType.PUBLISH)).append("\n")
                .append(answerService.get(REJECT_COUNT, language).getText()).append(" ")
                .append(incidentService.size(IncidentType.REJECT)).append("\n")
                .append(answerService.get(DELETE_COUNT, language).getText()).append(" ")
                .append(incidentService.size(IncidentType.DELETE)).append("\n")
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
