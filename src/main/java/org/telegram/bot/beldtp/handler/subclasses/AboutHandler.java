package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "about", accessRight = UserRole.USER)
public class AboutHandler extends Handler {

    private static final String ADMIN_USERNAME = "adminUsername";

    @Value("${beldtp.admin.username}")
    private String adminUsername;

    @Autowired
    private AnswerService answerService;

    @Override
    public String getText(User user, Update update) {
        return super.getText(user, update)
                + "\n\n"
                + answerService.get(ADMIN_USERNAME, user.getLanguage()).getText()
                + " @" + adminUsername;
    }
}
