package org.telegram.bot.beldtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        try {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(update.getMessage().getFrom().getId().longValue());
            sendMessage.setText(update.getMessage().getText());

            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message", e);
        }
    }

    @PostConstruct
    public void start() {
        LOGGER.info("username: {}, token: {}", username, token);
    }
}
