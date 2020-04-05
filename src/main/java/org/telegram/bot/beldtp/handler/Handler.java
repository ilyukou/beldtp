package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class Handler {

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
    private byte maxHandlerInRow;

    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user){
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        markupInline.setKeyboard(getAvailableHandlerForUser(user.getRole())
//                .parallelStream()
//                .map(handler -> Collections.singletonList(new InlineKeyboardButton()
//                        .setText(answerService.get(handler.getType(), user.getLanguage()).getLabel())
//                        .setCallbackData(answerService.get(handler.getType(), user.getLanguage()).getType())))
//                .collect(Collectors.toList()));

        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        List<Handler> handlers = getAvailableHandlerForUser(user.getRole());

        for (int i = 0; i < handlers.size() ; i += getMaxHandlerInRow()) {
            List<InlineKeyboardButton> row = new LinkedList<>();

            if(i + getMaxHandlerInRow() > handlers.size()){
                for (int j = i; j < handlers.size(); j++) {
                    row.add(
                            new InlineKeyboardButton()
                                    .setText(answerService.get(handlers.get(j).getType(),
                                            user.getLanguage()).getLabel())
                                    .setCallbackData(answerService.get(handlers.get(j).getType(),
                                            user.getLanguage()).getType()));
                }
            } else {
                for (int j = i; j < i + getMaxHandlerInRow(); j++) {
                    row.add(
                            new InlineKeyboardButton()
                                    .setText(answerService.get(handlers.get(j).getType(),
                                            user.getLanguage()).getLabel())
                                    .setCallbackData(answerService.get(handlers.get(j).getType(),
                                            user.getLanguage()).getType()));
                }

            }

            buttons.add(row);
        }

        return new InlineKeyboardMarkup().setKeyboard(buttons);
    }

    public TelegramResponse getMessage(User user, Update update) {

        InlineKeyboardMarkup markupInline  = getInlineKeyboardMarkup(user);

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();

            editMessageText.setChatId(user.getId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText());
            editMessageText.setReplyMarkup(markupInline);

            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getId());
        sendMessage.setText(getAnswer(user.getLanguage()).getText());
        sendMessage.setReplyMarkup(markupInline);

        return new TelegramResponse(sendMessage);
    }

    public TelegramResponse handle(User user, Update update){
        return transaction(user,update);
    }

    public TelegramResponse transaction(User user, Update update){

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
        return handlerMap.get(peekStatus);
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

    public byte getMaxHandlerInRow() {
        return maxHandlerInRow;
    }

    public void setMaxHandlerInRow(byte maxHandlerInRow) {
        this.maxHandlerInRow = maxHandlerInRow;
    }
}
