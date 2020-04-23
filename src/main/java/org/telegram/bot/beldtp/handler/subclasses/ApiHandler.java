package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "api", accessRight = UserRole.USER)
public class ApiHandler extends Handler {

    @Value("${beldtp-api.ip}")
    String beldtpApiIp;

    @Override
    public String getText(User user, Update update) {
        return super.getText(user, update)
                + "\n\n" +
                "IP " + beldtpApiIp;
    }
}
