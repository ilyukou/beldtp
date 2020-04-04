package org.telegram.bot.beldtp.handler.subclasses.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "changeUserRole", accessRight = UserRole.USER)
public class ChangeUserRoleHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    private TelegramResponse removeFromHandler(User user, Update update) {
        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }
        user = userService.save(user);

        return super.getMessage(user, update);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        if (!update.hasCallbackQuery() || !update.getCallbackQuery().getData().contains("-")) {
            return removeFromHandler(user, update);
        }

        String data = update.getCallbackQuery().getData();
        String userRole = data.split("-")[0];
        Long id = Long.valueOf(data.split("-")[1]);

        User foundUser = userService.get(id);

        if (foundUser == null) {
            return removeFromHandler(user, update);
        }

        if (foundUser.getId().equals(user.getId())) {
            telegramResponseBlockingQueue.push(
                    new TelegramResponse(
                            new AnswerCallbackQuery()
                                    .setCallbackQueryId(update.getCallbackQuery().getId())
                                    .setText("You cannot change you're role")
                    ));

            return removeFromHandler(user, update);
        }

        for (UserRole role : UserRole.values()) {
            if (role.name().equals(userRole)) {
                foundUser.setRole(role);
                foundUser = userService.save(foundUser);
                telegramResponseBlockingQueue.push(
                        new TelegramResponse(
                                new AnswerCallbackQuery()
                                        .setCallbackQueryId(update.getCallbackQuery().getId())
                                        .setText("Set " + role.name() + " to @" + foundUser.getUsername())
                        ));
            }
        }

        return removeFromHandler(user, update);
    }
}
