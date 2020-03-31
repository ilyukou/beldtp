package org.telegram.bot.beldtp.listener.telegramResponse;


import org.telegram.bot.beldtp.model.TelegramResponse;

public interface TelegramResponseBlockingQueue {
    TelegramResponse takeAndRemove();

    void push(TelegramResponse telegramResponse);

    TelegramResponse peek();

    int size();
}
