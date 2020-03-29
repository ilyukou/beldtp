package org.telegram.bot.beldtp.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@Component
public class TelegramResponse<T extends Serializable> {

    private PartialBotApiMethod<T> partialBotApiMethod;

    private BotApiMethod<T> botApiMethod;

    private Update update;

    public TelegramResponse(PartialBotApiMethod<T> partialBotApiMethod, Update update) {
        this.partialBotApiMethod = partialBotApiMethod;
        this.update = update;
    }

    public TelegramResponse(BotApiMethod<T> botApiMethod, Update update) {
        this.botApiMethod = botApiMethod;
        this.update = update;
    }

    public TelegramResponse() {

    }

    public PartialBotApiMethod<T> getPartialBotApiMethod() {
        return partialBotApiMethod;
    }

    public void setPartialBotApiMethod(PartialBotApiMethod<T> partialBotApiMethod) {
        this.partialBotApiMethod = partialBotApiMethod;
    }

    public BotApiMethod<T> getBotApiMethod() {
        return botApiMethod;
    }

    public void setBotApiMethod(BotApiMethod<T> botApiMethod) {
        this.botApiMethod = botApiMethod;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }
}
