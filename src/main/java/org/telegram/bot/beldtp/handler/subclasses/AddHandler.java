package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.AddMediaHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "add", accessRight = UserRole.USER)
public class AddHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AddMediaHandler addMediaHandler;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        user.pushStatus(addMediaHandler.getType());
        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        if (update.hasCallbackQuery()){
            telegramResponseBlockingQueue.push(
                    new TelegramResponse(
                            new AnswerCallbackQuery()
                                .setCallbackQueryId(update.getCallbackQuery().getId())
                                    .setText("Incident add"))
            );
        }

        if(user.peekStatus().equals(getType())){
            user.popStatus();
        }

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user,update);
    }
}
