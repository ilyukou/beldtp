package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "main", accessRight = UserRole.USER, maxHandlerInRow = 2)
public class MainHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private AddHandler addHandler;

    @Autowired
    private HelpHandler helpHandler;

    @Autowired
    private SettingHandler settingHandler;

    @Autowired
    private QueueHandler queueHandler;

    @Autowired
    private ConfigurationHandler configurationHandler;

    @Autowired
    private AboutHandler aboutHandler;

    @Autowired
    private ProfileHandler profileHandler;

    @Autowired
    private ApiHandler apiHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        return super.getMessage(user, update);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        return transaction(user,update);
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(addHandler, helpHandler, settingHandler,
                aboutHandler, profileHandler, apiHandler,
                queueHandler, configurationHandler);
    }
}
