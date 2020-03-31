package org.telegram.bot.beldtp.listener.telegramResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.TelegramResponse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TelegramResponseBlockingQueueImpl implements TelegramResponseBlockingQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramResponseBlockingQueueImpl.class);
    private static BlockingQueue<TelegramResponse> blockingQueue = new LinkedBlockingQueue<>();

    @Override
    public TelegramResponse takeAndRemove() {
        return blockingQueue.poll();
    }

    @Override
    public void push(TelegramResponse telegramResponse) {
        try {
            blockingQueue.put(telegramResponse);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted Exception when put into Blocking Queue Song", e);
        }
    }

    @Override
    public TelegramResponse peek() {
        return blockingQueue.peek();
    }

    @Override
    public int size() {
        return blockingQueue.size();
    }
}
