package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.InlineKeyboardMarkupUtil;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class Handler {

    private static final String DEFAULT_HANDLER = "main";

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private HandlerMap handlerMap;

    @Autowired
    private BackHandler backHandler;

    private UserRole accessRight;

    private String type;

    private byte maxButtonInRow;
    private static final String START_HANDLER_TYPE = "start";

    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        return InlineKeyboardMarkupUtil
                .getMarkup(
                        getAvailableHandlerForUser(user.getRole()),
                        user, update,
                        getMaxButtonInRow());
    }

    public List<TelegramResponse> getSendMessage(User user, Update update) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getId());
        sendMessage.setText(getText(user, update));
        sendMessage.setReplyMarkup(getInlineKeyboardMarkup(user, update));
        sendMessage.setParseMode(getParseMode(user, update));

        return Arrays.asList(new TelegramResponse(sendMessage));
    }

    public List<TelegramResponse> getEditMessageText(User user, Update update) {
        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(user.getId());
        editMessageText.setText(getText(user, update));
        editMessageText.setReplyMarkup(getInlineKeyboardMarkup(user, update));

        editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setParseMode(getParseMode(user, update));

        return Arrays.asList(new TelegramResponse(editMessageText, update));
    }

    public List<TelegramResponse> getMessage(User user, Update update) {

        if (update.hasCallbackQuery()) {
            return getEditMessageText(user, update);
        }

        return getSendMessage(user, update);
    }

    public String getText(User user, Update update) {
        return getAnswer(user.getLanguage()).getText();
    }

    public String getLabel(User user, Update update) {
        return getAnswer(user.getLanguage()).getLabel();
    }

    public String getParseMode(User user, Update update) {
        return ParseMode.MARKDOWN;
    }

    public List<TelegramResponse> handle(User user, Update update) {
        return transaction(user, update);
    }

    public List<TelegramResponse> transaction(User user, Update update) {

        if (update.hasCallbackQuery()) {
            for (Handler handler : getAvailableHandlerForUser(user.getRole())) {

                if (update.getCallbackQuery().getData().equals(handler.getAnswer(user.getLanguage()).getType())) {
                    user.pushStatus(handler.getAnswer(user.getLanguage()).getType());

                    user = userService.save(user);

                    return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
                }
            }
        }

        return null;
    }

    public Handler getHandlerByStatus(String peekStatus) {
        Handler handler = handlerMap.get(peekStatus);

        if (handler != null){
            return handler;
        }

        return handlerMap.get(DEFAULT_HANDLER);
    }

    public final List<Handler> getAvailableHandlerForUser(UserRole role) {
        return getChild()
                .stream()
                .filter(logicComponent -> role.getValue() >= logicComponent.getAccessRight().getValue())
                .collect(Collectors.toList());
    }

    public List<Handler> getChild() {
        return Collections.singletonList(backHandler);
    }

    public Answer getAnswer(Language language) {
        return answerService.get(getType(), language);
    }

    public UserRole getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(UserRole accessRight) {
        this.accessRight = accessRight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte getMaxButtonInRow() {
        return maxButtonInRow;
    }

    public void setMaxButtonInRow(byte maxButtonInRow) {
        this.maxButtonInRow = maxButtonInRow;
    }
}
