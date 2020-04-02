package org.telegram.bot.beldtp.handler.subclasses;

import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@HandlerInfo(type = "profile", accessRight = UserRole.USER)
public class ProfileHandler extends Handler {
    @Override
    public TelegramResponse getMessage(User user, Update update) {

        StringBuilder builder = new StringBuilder();

        builder.append(getAnswer(user.getLanguage()).getLabel()).append("\n");
        builder.append("\n");

        if(user.getFirstName() != null){
            builder.append(user.getFirstName());
        }

        if(user.getLastName() != null){
            builder.append(" ").append(user.getLastName()).append("\n");
        }

        if(user.getUsername() != null){
            builder.append("@").append(user.getUsername()).append("\n").append("\n");
        }

        if(user.getLanguage() != null){
            builder.append(user.getLanguage().getValue());
        }

        if(update.hasCallbackQuery()){
            EditMessageText message = new EditMessageText();

            message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            message.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());

            message.setChatId(user.getId());
            message.setText(builder.toString());
            message.setReplyMarkup(getInlineKeyboardMarkup(user));
            return new TelegramResponse(message,update);
        }

        SendMessage message = new SendMessage();

        message.setChatId(user.getId());
        message.setText(builder.toString());
        message.setReplyMarkup(getInlineKeyboardMarkup(user));
        return new TelegramResponse(message);
    }
}
