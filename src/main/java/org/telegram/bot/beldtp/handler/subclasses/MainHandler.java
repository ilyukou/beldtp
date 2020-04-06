package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "main", accessRight = UserRole.USER, maxButtonInRow = 2)
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
    public List<Handler> getChild() {
        return Arrays.asList(addHandler, helpHandler, settingHandler,
                aboutHandler, profileHandler, apiHandler,
                queueHandler, configurationHandler);
    }
}
