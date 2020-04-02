package org.telegram.bot.beldtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.ExceptionHandler;
import org.telegram.bot.beldtp.handler.subclasses.StartHandler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class BeldtpBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeldtpBot.class);

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private UserService userService;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

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

        User user = userService.get(UpdateUtil.getChatId(update));

        TelegramResponse response;

        if(user == null){
            response = startHandler.getMessage(null, update);
        } else {
            response = startHandler
                        .getHandlerByStatus(user.peekStatus())
                        .handle(user,update);
        }

        if(response == null){
            executeTelegramResponse(exceptionHandler.getMessage(userService.get(UpdateUtil.getChatId(update)),update));
        } else {
            executeTelegramResponse(response);
        }
    }

    @PostConstruct
    public void start() {
        LOGGER.info("username: {}, token: {}", username, token);
    }

    public void executeTelegramResponse(TelegramResponse telegramResponse) {
        try {

            if (telegramResponse.hasSendMessage()) {
                execute(telegramResponse.getSendMessage());

            } else if (telegramResponse.hasAnswerCallbackQuery()) {
                execute(telegramResponse.getAnswerCallbackQuery());

            } else if (telegramResponse.hasEditMessageText()) {

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

                LOGGER.error("TelegramResponse is null");
            }
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message", e);
        }
    }
}
