package org.telegram.bot.beldtp.handler.subclasses;

import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "queue", accessRight = UserRole.USER)
public class QueueHandler extends Handler {
    @Override
    public TelegramResponse handle(User user, Update update) {
        return null;
    }
}
