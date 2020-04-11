package org.telegram.bot.beldtp.handler.subclasses.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@HandlerInfo(type = "changeUserRole", accessRight = UserRole.USER)
public class ChangeUserRoleHandler extends Handler {

    private static final String YOU_CANNOT_CHANGE_YOURE_ROLE = "youCannotChangeYoureRole";
    private static final String SET = "set";

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    private List<TelegramResponse> removeFromHandler(List<TelegramResponse> responses,
                                                     User user, Update update) {
        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }
        user = userService.save(user);

        return super.getMessage(responses, user, update);
    }

    @Override
    public List<TelegramResponse> handle(List<TelegramResponse> responses, User user, Update update) {
        if (!update.hasCallbackQuery() || !update.getCallbackQuery().getData().contains("-")) {
            return removeFromHandler(responses,user, update);
        }

        String data = update.getCallbackQuery().getData();
        String userRole = data.split("-")[0];
        Long id = Long.valueOf(data.split("-")[1]);

        User foundUser = userService.get(id);

        if (foundUser == null) {
            return removeFromHandler(responses, user, update);
        }

        if (foundUser.getId().equals(user.getId())) {
            responses.add(
                    new TelegramResponse(
                            new AnswerCallbackQuery()
                                    .setCallbackQueryId(update.getCallbackQuery().getId())
                                    .setText(answerService
                                            .get(YOU_CANNOT_CHANGE_YOURE_ROLE, user.getLanguage()).getText())
                    ));

            return removeFromHandler(responses, user, update);
        }

        for (UserRole role : UserRole.values()) {
            if (role.name().equals(userRole)) {
                foundUser.setRole(role);
                foundUser = userService.save(foundUser);
                responses.add(
                        new TelegramResponse(
                                new AnswerCallbackQuery()
                                        .setCallbackQueryId(update.getCallbackQuery().getId())
                                        .setText(answerService
                                                .get(SET, user.getLanguage()) + role.name()
                                                + " @" + foundUser.getUsername())
                        ));
            }
        }

        return removeFromHandler(responses, user, update);
    }
}
