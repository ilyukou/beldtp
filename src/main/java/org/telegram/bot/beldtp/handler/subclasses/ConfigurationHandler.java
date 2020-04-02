package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.configuration.PrivateStatisticsHandler;
import org.telegram.bot.beldtp.handler.subclasses.configuration.PublicStatisticsHandler;
import org.telegram.bot.beldtp.model.UserRole;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "configuration", accessRight = UserRole.ADMIN)
public class ConfigurationHandler extends Handler {

    @Autowired
    private PrivateStatisticsHandler privateStatisticsHandler;

    @Autowired
    private PublicStatisticsHandler publicStatisticsHandler;

    @Autowired
    private BackHandler backHandler;

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(publicStatisticsHandler,privateStatisticsHandler,backHandler);
    }
}
