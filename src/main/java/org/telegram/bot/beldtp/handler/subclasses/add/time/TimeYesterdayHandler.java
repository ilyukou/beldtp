package org.telegram.bot.beldtp.handler.subclasses.add.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.DayTimeHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.HourTimeHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;

@HandlerInfo(type = "timeYesterday", accessRight = UserRole.USER)
public class TimeYesterdayHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private HourTimeHandler timeHourLogicComponent;

    @Autowired
    private DayTimeHandler timeDayLogicComponent;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        Time time = new Time();
        Incident incident = incidentService.getDraft(user);

        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.DATE) > 1){
            time.setYear(calendar.get(Calendar.YEAR));
            time.setMonth((byte) calendar.get(Calendar.MONTH));
            time.setDay((byte) calendar.get(Calendar.DATE));

        } else { // today first day in month
           if(calendar.get(Calendar.MONTH) > 0){ // 0 - January, 1 - February
               // this year, not first month
               time.setYear(calendar.get(Calendar.YEAR));
               time.setMonth((byte) (calendar.get(Calendar.MONTH) - 1));
               time.setDay((byte)
                       timeDayLogicComponent.getCountDayInMonth(
                               calendar.get(Calendar.MONTH),
                               calendar.get(Calendar.YEAR)));
           } else {
             // today first day of first month in this year (1.1.2020) - i need (31.12.2019)
               time.setYear(calendar.get(Calendar.YEAR) - 1);
               time.setMonth((byte) 11);
               time.setDay((byte)
                       timeDayLogicComponent.getCountDayInMonth(
                               11,
                               calendar.get(Calendar.YEAR) - 1));
           }


        }

        if(update.hasCallbackQuery()){
            telegramResponseBlockingQueue.push(
                    new TelegramResponse(new AnswerCallbackQuery()
                            .setCallbackQueryId(update.getCallbackQuery().getId()))
            );
        }

        user.popStatus();
        user.pushStatus(timeHourLogicComponent.getType());
        user = userService.save(user);

        incident.setTime(time);
        incident = incidentService.save(incident);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
