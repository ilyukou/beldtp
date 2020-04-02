package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeNowHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeSelectHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeTodayHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeYesterdayHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addTime", accessRight = UserRole.USER)
public class AddTimeHandler extends Handler {
    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TimeNowHandler timeNowHandler;

    @Autowired
    private TimeSelectHandler timeSelectHandler;

    @Autowired
    private TimeTodayHandler timeTodayHandler;

    @Autowired
    private TimeYesterdayHandler timeYesterdayHandler;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        Incident incident = incidentService.getDraft(user);

        if (incident.getTime() == null
                || incident.getTime().getYear() == null
                || incident.getTime().getMonth() == null
                || incident.getTime().getDay() == null
                || incident.getTime().getHour() == null
                || incident.getTime().getMinute() == null ) {
            return super.getMessage(user, update);

        } else {
            user.popStatus();
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(timeNowHandler,
                timeTodayHandler,
                timeYesterdayHandler,
                timeSelectHandler);
    }
}
