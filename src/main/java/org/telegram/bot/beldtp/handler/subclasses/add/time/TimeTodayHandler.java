package org.telegram.bot.beldtp.handler.subclasses.add.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.HourTimeHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;

@HandlerInfo(type = "timeToday", accessRight = UserRole.USER)
public class TimeTodayHandler extends Handler {
    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private HourTimeHandler timeHourLogicComponent;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Time time = new Time();
        Incident incident = incidentService.getDraft(user);

        Calendar calendar = Calendar.getInstance();

        time.setYear(calendar.get(Calendar.YEAR));
        time.setMonth((byte) calendar.get(Calendar.MONTH));
        time.setDay((byte) calendar.get(Calendar.DATE));

        if(getType().equals(user.peekStatus())){
            user.popStatus();
        }

        user.pushStatus(timeHourLogicComponent.getType());
        user = userService.save(user);

        incident.setTime(time);
        incident = incidentService.save(incident);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
