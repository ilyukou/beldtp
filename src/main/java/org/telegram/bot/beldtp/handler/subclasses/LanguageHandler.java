package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.stream.Collectors;

@HandlerInfo(type = "language", accessRight = UserRole.USER)
public class LanguageHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        Language[] languages = Language.values();

        if(user.getLanguage() == null){
            user.setLanguage(Language.BE);
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline
                .setKeyboard(Arrays.stream(languages)
                        .map(language -> Arrays.asList(new InlineKeyboardButton()
                        .setCallbackData(language.toString())
                        .setText(language.getValue())))
                        .collect(Collectors.toList()));

        if (update.hasCallbackQuery()) {
            EditMessageText editMessageText = new EditMessageText();

            editMessageText.setChatId(user.getId());
            editMessageText.setText(answerService.get(getType(), user.getLanguage()).getText());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setReplyMarkup(markupInline);

            return new TelegramResponse(editMessageText);
        } else {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(user.getId());
            sendMessage.setText(answerService.get(getType(), Language.BE).getText());

            sendMessage.setReplyMarkup(markupInline);

            return new TelegramResponse(sendMessage);
        }
    }

    @Override
    public TelegramResponse handle(User user, Update update) {

        Language[] languages = Language.values();

        for (Language language : languages) {
            if (language.toString().equals(update.getCallbackQuery().getData())) {
                user.setLanguage(language);
                break;
            }
        }

        if(user.peekStatus().equals(getType())){
            user.popStatus();
        }

        user = userService.save(user);

        Answer languageSuccess = answerService.get(getType()+"Success", user.getLanguage());

        telegramResponseBlockingQueue.push(
                new TelegramResponse(
                        new AnswerCallbackQuery()
                                .setText(languageSuccess.getText())
                                .setCallbackQueryId(update.getCallbackQuery().getId())
                ));

        return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
