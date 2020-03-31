package org.telegram.bot.beldtp.listener.telegramResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.BeldtpBot;
import org.telegram.bot.beldtp.model.TelegramResponse;

@Component
public class TelegramResponseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramResponseListener.class);

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Autowired
    private BeldtpBot bot;

    @Scheduled(fixedRate = 5)
    private void executeTelegramResponse() {

        while (telegramResponseBlockingQueue.size() > 0) {
            TelegramResponse telegramResponse = null;

            try {
                telegramResponse = telegramResponseBlockingQueue.takeAndRemove();

                bot.executeTelegramResponse(telegramResponse);

            } catch (Exception e) {
                telegramResponseBlockingQueue.push(telegramResponse);
                LOGGER.warn("Error while send TelegramResponse", e);
            }
        }
    }
}
