package org.telegram.bot.beldtp.handler.subclasses.add;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.MainHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@HandlerInfo(type = "confirm", accessRight = UserRole.USER)
public class ConfirmAddHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmAddHandler.class);

    private static final String REQUIRED_MIN_ONE_MEDIA = "requiredMinOneAttachmentFile";
    private static final String REQUIRED_TEXT = "requiredText";
    private static final String REQUIRED_LOCATION = "requiredLocation";
    private static final String REQUIRED_TIME = "requiredTime";
    private static final String YOU_INCIDENT_BUILD = "youIncidentBuild";

    private static final String MEDIA_WAS_OLD = "attachmentFileWasOld";
    private static final double HOUR_WHEN_MEDIA_SET_OLD = 20;
    private static final long HOUR_IN_MILLIS = 3600000;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private MainHandler mainHandler;

    @Override
    public String getLabel(User user, Update update) {
        return EmojiUtil.CHECK_MARK + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        return handle(user, update);
    }

    private User removeThisHandler(User user){
        if (user.peekStatus().equals(getType())) {
            user.popStatus();
            user = userService.save(user);
            return user;
        }

        return user;
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (!update.hasCallbackQuery() || draft == null) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
                user = userService.save(user);
            }

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }
        List<TelegramResponse> responses = new ArrayList<>();

        if (draft.getAttachmentFiles() == null || draft.getAttachmentFiles().size() == 0) {
            user = removeThisHandler(user);

            responses.add(getAnswerCallbackQuery(answerService
                    .get(REQUIRED_MIN_ONE_MEDIA, user.getLanguage()).getText(), user, update));
            return responses;
        }

        long nowDate = Calendar.getInstance().getTimeInMillis();
        for (AttachmentFile attachmentFile : draft.getAttachmentFiles()){
            if(nowDate - HOUR_IN_MILLIS * HOUR_WHEN_MEDIA_SET_OLD > attachmentFile.getUploadDate()){
                draft.getAttachmentFiles().clear();
                draft = incidentService.save(draft);

                responses.add(getAnswerCallbackQuery(answerService
                        .get(MEDIA_WAS_OLD, user.getLanguage()).getText(), user, update));

                user = removeThisHandler(user);

                responses.addAll(super.getHandlerByStatus(user.peekStatus()).getMessage(user, update));
                return responses;
            }
        }

        if (draft.getText() == null || draft.getText().length() == 0) {
            user = removeThisHandler(user);

            responses.add(getAnswerCallbackQuery(answerService
                    .get(REQUIRED_TEXT, user.getLanguage()).getText(), user, update));
            return responses;
        }

        if (draft.getLocation() == null
                || draft.getLocation().getLongitude() == null
                || draft.getLocation().getLatitude() == null) {
            user = removeThisHandler(user);

            responses.add(getAnswerCallbackQuery(answerService
                    .get(REQUIRED_LOCATION, user.getLanguage()).getText(), user, update));
            return responses;
        }

        if (draft.getTime() == null
                || draft.getTime().getYear() == null
                || draft.getTime().getMonth() == null
                || draft.getTime().getDay() == null
                || draft.getTime().getHour() == null
                || draft.getTime().getMinute() == null) {
            user = removeThisHandler(user);

            responses.add(getAnswerCallbackQuery(answerService
                    .get(REQUIRED_TIME, user.getLanguage()).getText(), user, update));
            return responses;
        }

        draft.setType(IncidentType.BUILD);
        draft = incidentService.save(draft);

        while (!user.peekStatus().equals(mainHandler.getType())) {
            user.popStatus();
        }

        user = userService.save(user);
        responses.add(getAnswerCallbackQuery(answerService
                .get(YOU_INCIDENT_BUILD, user.getLanguage()).getText(), user, update));

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private TelegramResponse getAnswerCallbackQuery(String text, User user, Update update) {
        return new TelegramResponse(
                new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
                        .setText(text));
    }
}