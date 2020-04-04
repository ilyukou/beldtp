package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.queue.ReadyQueueHandler;
import org.telegram.bot.beldtp.handler.subclasses.queue.RejectQueueHandler;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "queue", accessRight = UserRole.MODERATOR)
public class QueueHandler extends Handler {

    @Autowired
    private ReadyQueueHandler readyQueueHandler;

    @Autowired
    private RejectQueueHandler rejectQueueHandler;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        StringBuilder builder = new StringBuilder();

        builder.append(getAnswer(user.getLanguage()).getText()).append("\n");
        builder.append("\n");

        builder.append("Ready size ").append(incidentService.get(IncidentType.READY).size()).append("\n");

        if (user.getRole().getValue() >= UserRole.ADMIN.getValue()) {
            builder.append("Reject size ").append(incidentService.get(IncidentType.REJECT).size()).append("\n");
        }

        TelegramResponse response = super.getMessage(user, update);

        if (response.hasEditMessageText()) {

            EditMessageText editMessageText = response.getEditMessageText();
            editMessageText.setText(builder.toString());

            return new TelegramResponse(editMessageText, update);

        } else if (response.hasSendMessage()) {

            SendMessage sendMessage = response.getSendMessage();
            sendMessage.setText(builder.toString());

            return new TelegramResponse(sendMessage);

        }

        return super.getMessage(user, update);
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(readyQueueHandler, rejectQueueHandler, backHandler);
    }
}
