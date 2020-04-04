package org.telegram.bot.beldtp.handler.subclasses.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "rejectQueue", accessRight = UserRole.ADMIN)
public class RejectQueueHandler extends Handler {

    private static final String CONFIRM = "Confirm";

    private static final String REJECT = "Reject";

    private static final IncidentType REJECT_INCIDENT_TYPE = IncidentType.REJECT;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private BackHandler backHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        List<Incident> incidents = incidentService.get(REJECT_INCIDENT_TYPE);

        if (incidents == null || incidents.size() == 0) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
            }
            user = userService.save(user);

            return new TelegramResponse(new AnswerCallbackQuery()
                    .setText("Reject incidents size is 0")
                    .setCallbackQueryId(update.getCallbackQuery().getId()));
        }

        SendMediaGroup sendMediaGroup = incidentService.getSendMediaGroup(incidents.get(0));
        sendMediaGroup.setChatId(user.getId());

        telegramResponseBlockingQueue.push(new TelegramResponse(sendMediaGroup));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setText("Verify ?");
        sendMessage.setReplyMarkup(getReplyMarkup(incidents.get(0), user));

        return new TelegramResponse(sendMessage);
    }

    private InlineKeyboardMarkup getReplyMarkup(Incident incident, User user) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(CONFIRM)
                .setCallbackData(CONFIRM + "-" + incident.getId())));

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(REJECT)
                .setCallbackData(REJECT + "-" + incident.getId())));

        buttons.add(Collections.singletonList(new InlineKeyboardButton()
                .setText(backHandler.getAnswer(user.getLanguage()).getText())
                .setCallbackData(backHandler.getAnswer(user.getLanguage()).getType())));

        return markup.setKeyboard(buttons);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        TelegramResponse transition = transaction(user, update);

        if (transition != null) {
            return transition;
        }

        if (!update.hasCallbackQuery()) {
            return null;
        }

        if (!(update.getCallbackQuery().getData().contains(CONFIRM)
                || update.getCallbackQuery().getData().contains(REJECT))) {
            return null;
        }

        String data = update.getCallbackQuery().getData();

        String method = data.split("-")[0];
        Long id = Long.parseLong(data.split("-")[1]);

        Incident incident = incidentService.get(id);

        if (method.equals(CONFIRM)) {
            incident.setType(IncidentType.VERIFY);
        }

        if (method.equals(REJECT)) {
            incident.setType(IncidentType.REJECT);
        }

        incident = incidentService.save(incident);

        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
