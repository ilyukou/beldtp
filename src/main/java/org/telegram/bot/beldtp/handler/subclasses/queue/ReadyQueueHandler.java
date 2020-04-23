package org.telegram.bot.beldtp.handler.subclasses.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "readyQueue", accessRight = UserRole.MODERATOR)
public class ReadyQueueHandler extends Handler {

    private static final String VERIFY_BUTTON = "verifyButton";
    private static final String REJECT_BUTTON = "rejectButton";
    private static final String NOT_READY_INCIDENT = "notReadyIncident";

    private static final IncidentType READY_INCIDENT_TYPE = IncidentType.READY;

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private BackHandler backHandler;

    @Override
    public String getLabel(User user, Update update) {
        return new StringBuilder().append(incidentService.get(IncidentType.READY).size()).append(" | ")
                .append(getAnswer(user.getLanguage()).getLabel()).toString();
    }

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        List<Incident> incidents = incidentService.get(READY_INCIDENT_TYPE);

        if (incidents == null || incidents.size() == 0) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
            }
            user = userService.save(user);


            return Arrays.asList(new TelegramResponse(new AnswerCallbackQuery()
                    .setText(answerService.get(NOT_READY_INCIDENT, user.getLanguage()).getText())
                    .setCallbackQueryId(update.getCallbackQuery().getId())));
        }

        SendMediaGroup sendMediaGroup = incidentService.getSendMediaGroup(incidents.get(0));
        sendMediaGroup.setChatId(user.getId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setText(getAnswer(user.getLanguage()).getText());
        sendMessage.setReplyMarkup(getReplyMarkup(incidents.get(0), user));

        return Arrays.asList(new TelegramResponse(sendMediaGroup),new TelegramResponse(sendMessage));
    }

    private InlineKeyboardMarkup getReplyMarkup(Incident incident, User user) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(answerService.get(VERIFY_BUTTON, user.getLanguage()).getLabel())
                .setCallbackData(VERIFY_BUTTON + "-" + incident.getId())));

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(answerService.get(REJECT_BUTTON, user.getLanguage()).getLabel())
                .setCallbackData(REJECT_BUTTON + "-" + incident.getId())));

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(backHandler.getAnswer(user.getLanguage()).getText())
                .setCallbackData(backHandler.getAnswer(user.getLanguage()).getType())));

        return markup.setKeyboard(buttons);
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {
        List<TelegramResponse> transition = transaction(user, update);

        if (transition != null) {
            return transition;
        }

        if (!isValid(user, update)) {
            throw new BadRequestException();
        }

        String data = update.getCallbackQuery().getData();

        String method = data.split("-")[0];
        Long id = Long.parseLong(data.split("-")[1]);

        Incident incident = incidentService.get(id);

        if (method.equals(VERIFY_BUTTON)) {
            incident.setType(IncidentType.VERIFY);
        }

        if (method.equals(REJECT_BUTTON)) {
            incident.setType(IncidentType.REJECT);
        }

        incident = incidentService.save(incident);

        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    private boolean isValid(User user, Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }

        if (!(update.getCallbackQuery().getData().contains(VERIFY_BUTTON)
                || update.getCallbackQuery().getData().contains(REJECT_BUTTON))) {
            return false;
        }

        try {
            String data = update.getCallbackQuery().getData();
            String method = data.split("-")[0];
            Long id = Long.parseLong(data.split("-")[1]);

            if (!(method.equals(VERIFY_BUTTON) || method.equals(REJECT_BUTTON))) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
