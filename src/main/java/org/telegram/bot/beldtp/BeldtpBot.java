package org.telegram.bot.beldtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.UpdateHandler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class BeldtpBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeldtpBot.class);


    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Autowired
    private UpdateHandler updateHandler;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.handle(update);
    }

    @PostConstruct
    public void start() {
        LOGGER.info("username: {}, token: {}", username, token);
    }

    public void executeTelegramResponse(TelegramResponse telegramResponse) throws TelegramApiException {
        if (telegramResponse.hasSendMessage()) {
            execute(telegramResponse.getSendMessage());

        } else if (telegramResponse.hasAnswerCallbackQuery()) {
            execute(telegramResponse.getAnswerCallbackQuery());

        } else if (telegramResponse.hasEditMessageText()) {

            // try to send AnswerCallbackQuery
            // ignore because if throw Exception, that indicate a call back query was answered
            try {
                execute(
                        new AnswerCallbackQuery()
                                .setCallbackQueryId(telegramResponse.getUpdate().getCallbackQuery().getId()));
            } catch (Exception e) {
                // ignore
            }
            execute(telegramResponse.getEditMessageText());

        } else if (telegramResponse.hasDeleteMessage()) {
            execute(telegramResponse.getDeleteMessage());

        } else if (telegramResponse.hasEditMessageReplyMarkup()) {
            execute(telegramResponse.getEditMessageReplyMarkup());

        } else if (telegramResponse.hasEditMessageText()) {
            execute(telegramResponse.getEditMessageText());

        } else if (telegramResponse.hasSendMediaGroup()) {
            execute(telegramResponse.getSendMediaGroup());

        } else {
            LOGGER.error("TelegramResponse is null", telegramResponse);
        }
    }
}
