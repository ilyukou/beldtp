package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.*;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Stack;

@HandlerInfo(type = "backAndRejectIncident", accessRight = UserRole.USER)
public class BackAndRejectIncidentHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MainHandler mainHandler;

    @Autowired
    private StartHandler startHandler;

    @Override
    public String getLabel(User user, Update update) {
        return EmojiUtil.CROSS_MARK + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (isValid(user)) {
            for (int i = 0; i < user.getStatus().size(); i++) {
                if (!user.peekStatus().equals(mainHandler.getType())) {
                    user.popStatus();
                } else {
                    break;
                }
            }
        } else {
            Stack<String> stack = new Stack<>();
            stack.push(startHandler.getType());
            user.setStatus(stack);
        }

        if(draft.getLocation() != null){
            locationService.delete(draft.getLocation());
        }

        user.remove(draft);
        user = userService.save(user);

        incidentService.delete(draft);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    boolean isValid(User user) {
        for (String string : user.getStatus()) {
            if (string.equals(mainHandler.getType())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {
        return getMessage(user, update);
    }
}
