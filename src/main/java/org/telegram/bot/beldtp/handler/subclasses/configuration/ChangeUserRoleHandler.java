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

import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "changeUserRole", accessRight = UserRole.USER)
public class ChangeUserRoleHandler extends Handler {

    private static final String YOU_CANNOT_CHANGE_YOURE_ROLE = "youCannotChangeYoureRole";
    private static final String SET = "set";

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    private List<TelegramResponse> removeFromHandler(User user, Update update) {
        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }
        user = userService.save(user);

        return super.getMessage(user, update);
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {
        if (!isValid(user, update)) {
            return removeFromHandler(user, update);
        }

        String data = update.getCallbackQuery().getData();
        String userRole = data.split("-")[0];
        Long id = Long.valueOf(data.split("-")[1]);

        User foundUser = userService.get(id);

        if (foundUser == null) {
            return removeFromHandler(user, update);
        }

        List<TelegramResponse> responses = new LinkedList<>();

        if (foundUser.getId().equals(user.getId())) {
            responses.add(
                    new TelegramResponse(
                            new AnswerCallbackQuery()
                                    .setCallbackQueryId(update.getCallbackQuery().getId())
                                    .setText(answerService
                                            .get(YOU_CANNOT_CHANGE_YOURE_ROLE, user.getLanguage()).getText())
                    ));

            responses.addAll(removeFromHandler(user, update));
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

        responses.addAll(removeFromHandler(user, update));
        return responses;
    }

    private boolean isValid(User user, Update update) {
        if (!update.hasCallbackQuery() || !update.getCallbackQuery().getData().contains("-")) {
            return false;
        }

        try {
            String data = update.getCallbackQuery().getData();
            String userRole = data.split("-")[0];
            Long id = Long.valueOf(data.split("-")[1]);

            if (id != null && (userRole != null || userRole.length() == 0)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
