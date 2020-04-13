package org.telegram.bot.beldtp.handler.subclasses.add.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.AddHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;
import java.util.List;

@HandlerInfo(type = "timeNow", accessRight = UserRole.USER)
public class TimeNowHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AddHandler addHandler;

    @Override
    public List<TelegramResponse> getMessage( User user, Update update) {

        Time time = new Time();

        Incident incident = incidentService.getDraft(user);

        Calendar calendar = Calendar.getInstance();

        time.setYear(calendar.get(Calendar.YEAR));
        time.setMonth((byte) calendar.get(Calendar.MONTH));
        time.setDay((byte) calendar.get(Calendar.DATE));
        time.setHour((byte) calendar.get(Calendar.HOUR_OF_DAY));
        time.setMinute((byte) calendar.get(Calendar.MINUTE));

        while (!user.peekStatus().equals(addHandler.getType())){
            user.popStatus();
        }

        user = userService.save(user);

        incident.setTime(time);
        incident = incidentService.save(incident);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
