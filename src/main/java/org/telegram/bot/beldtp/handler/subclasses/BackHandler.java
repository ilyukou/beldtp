package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "back", accessRight = UserRole.USER)
public class BackHandler extends Handler {

    @Autowired
    private UserService userService;

    @Override
    public String getLabel(User user, Update update) {
        return EmojiUtil.REVERSE_BUTTON + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        if (user.peekStatus().equals(getType())) {
            user.popStatus(); // pop back
        }

        user.popStatus(); // needed pop

        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        return getMessage(user, update);
    }
}
