package org.telegram.bot.beldtp.handler.subclasses;

import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "profile", accessRight = UserRole.USER)
public class ProfileHandler extends Handler {

    @Override
    public String getText(User user, Update update) {
        StringBuilder builder = new StringBuilder();

        builder.append(getAnswer(user.getLanguage()).getText()).append("\n");
        builder.append("\n");

        if (user.getFirstName() != null && user.getLastName() != null) {
            builder.append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");

        } else if (user.getFirstName() != null) {
            builder.append(user.getFirstName()).append("\n");

        } else if (user.getLastName() != null) {
            builder.append(" ").append(user.getLastName()).append("\n");
        }

        if (user.getUsername() != null) {
            builder.append("@").append(user.getUsername()).append("\n").append("\n");
        }

//        if (user.getLanguage() != null) {
//            builder.append(user.getLanguage().getValue());
//        }

        return builder.toString();
    }
}
