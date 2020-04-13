package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@HandlerInfo(type = "backAndRejectIncident", accessRight = UserRole.USER)
public class BackAndRejectIncidentHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private MainHandler mainHandler;

    @Override
    public String getLabel(User user, Update update) {
        return EmojiUtil.CROSS_MARK + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        Incident draft = incidentService.getDraft(user);


        while (!user.peekStatus().equals(mainHandler.getType())) {
            user.popStatus();
        }

        user.remove(draft);
        user = userService.save(user);

        incidentService.delete(draft);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {
        return getMessage(user, update);
    }
}
