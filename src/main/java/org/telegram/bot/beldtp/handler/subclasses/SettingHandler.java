package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "setting", accessRight = UserRole.USER)
public class SettingHandler extends Handler {

    @Autowired
    private LanguageHandler languageHandler;

    @Autowired
    private BackHandler backHandler;

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(languageHandler,backHandler);
    }
}
