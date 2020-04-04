package org.telegram.bot.beldtp.handler.subclasses.add;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.MainHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "confirm", accessRight = UserRole.USER)
public class ConfirmAddHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmAddHandler.class);

    private static final String PHOTO_EMOJI = "\uD83D\uDDBC";

    private static final String VIDEO_EMOJI = "\uD83D\uDCF9";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private MainHandler mainHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        return handle(user, update);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (!update.hasCallbackQuery() || draft == null) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
                user = userService.save(user);
            }

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        if (draft.getMedia() == null || draft.getMedia().size() == 0) {
            return getAnswerCallbackQuery("Required min a media", user, update);
        }

        if (draft.getText() == null || draft.getText().length() == 0) {
            return getAnswerCallbackQuery("Required text", user, update);
        }

        if (draft.getLocation() == null
                || draft.getLocation().getLongitude() == null
                || draft.getLocation().getLatitude() == null) {
            return getAnswerCallbackQuery("Required text", user, update);
        }

        if (draft.getTime() == null
                || draft.getTime().getYear() == null
                || draft.getTime().getMonth() == null
                || draft.getTime().getDay() == null
                || draft.getTime().getHour() == null
                || draft.getTime().getMinute() == null) {
            return getAnswerCallbackQuery("Required time", user, update);
        }

        draft.setType(IncidentType.BUILD);
        draft = incidentService.save(draft);

        while (!user.peekStatus().equals(mainHandler.getType())) {
            user.popStatus();
        }

        user = userService.save(user);
        telegramResponseBlockingQueue.push(getAnswerCallbackQuery("You incident build", user, update));

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private TelegramResponse getAnswerCallbackQuery(String text, User user, Update update) {
        if (user.peekStatus().equals(getType())) {
            user.popStatus();
            user = userService.save(user);
        }

        return new TelegramResponse(
                new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
                        .setText(text));
    }
}