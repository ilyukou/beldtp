package org.telegram.bot.beldtp.handler.subclasses.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@HandlerInfo(type = "userProfile", accessRight = UserRole.ADMIN)
public class UserProfileHandler extends Handler {

    private static final String NOT_FOUND_USER_WITH_SUCH_USERNAME = "notFoundUserWithSuchUsername";
    private static final String INCIDENT_SIZE = "incidentSize";
    private static final String SET = "set";

    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private ChangeUserRoleHandler changeUserRoleHandler;

    @Override
    public TelegramResponse handle(User user, Update update) {
        TelegramResponse transaction = transaction(user, update);

        if (transaction != null) {
            return transaction;
        }

        if (update.hasCallbackQuery()) {
            return changeUserRoleHandler.handle(user, update);
        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            User foundUser = userService.get(update.getMessage().getText());

            if (foundUser == null) {
                return getTelegramResponseWhenUserNotExist(update, user);
            } else {
                return getMessageWhenUserFound(update, user, foundUser);
            }
        } else {
            throw new BadRequestException();
        }
    }

    private TelegramResponse getMessageWhenUserFound(Update update, User admin, User foundUser) {
        TelegramResponse response = super.getMessage(admin, update);

        if (response.hasEditMessageText()) {
            EditMessageText message = response.getEditMessageText();
            message
                    .setText(
                            message.getText() + "\n\n" + getUserAsString(foundUser));
            message.setReplyMarkup(inlineKeyboardMarkup(admin, foundUser));
            return new TelegramResponse(message, update);
        }

        if (response.hasSendMessage()) {
            SendMessage message = response.getSendMessage();
            message.setText(
                    message.getText() + "\n\n" + getUserAsString(foundUser));
            message.setReplyMarkup(inlineKeyboardMarkup(admin, foundUser));
            return new TelegramResponse(message);
        }

        return response;
    }

    private TelegramResponse getTelegramResponseWhenUserNotExist(Update update, User user) {
        SendMessage message = new SendMessage();
        message.setText(answerService
                .get(NOT_FOUND_USER_WITH_SUCH_USERNAME, user.getLanguage()) + update.getMessage().getText());
        message.setChatId(user.getId());

        telegramResponseBlockingQueue.push(new TelegramResponse(message));

        return super.getMessage(user, update);
    }

    private String getUserAsString(User user) {
        StringBuilder builder = new StringBuilder();

        if (user.getFirstName() != null) {
            builder.append(user.getFirstName());
        }

        if (user.getLastName() != null) {
            builder.append(" ").append(user.getLastName()).append("\n");
        }

        if (user.getUsername() != null) {
            builder.append("@").append(user.getUsername()).append("\n").append("\n");
        }

        if (user.getLanguage() != null) {
            builder.append(user.getLanguage().getValue()).append("\n").append("\n");
        }

        if (user.getRole() != null) {
            builder.append(user.getRole()).append("\n").append("\n");
        }

        if (user.getIncident() != null) {
            builder.append(answerService.get(INCIDENT_SIZE, user.getLanguage()))
                    .append(user.getIncident().size());
        }

        return builder.toString();
    }

    private InlineKeyboardMarkup inlineKeyboardMarkup(User user, User foundUser) {
        List<Handler> handlers = getChild();

        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();

        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equals(UserRole.ADMIN)) {
                continue; // only one admin can be exist
            }

            buttons.add(Arrays.asList(new InlineKeyboardButton()
                    .setText(answerService.get(SET, user.getLanguage()) + " " + userRole.name().toLowerCase())
                    .setCallbackData(userRole.name() + "-" + foundUser.getId())));
        }

        for (Handler handler : handlers) {
            buttons.add(Arrays.asList(new InlineKeyboardButton()
                    .setText(handler.getAnswer(user.getLanguage()).getText())
                    .setCallbackData(handler.getType())));
        }

        return new InlineKeyboardMarkup().setKeyboard(buttons);
    }
}
