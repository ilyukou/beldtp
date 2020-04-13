package org.telegram.bot.beldtp.handler.subclasses.add.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.DayTimeHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.HourTimeHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        Time time = new Time();
        Incident incident = incidentService.getDraft(user);

        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.DATE) > 1){
            time.setYear(calendar.get(Calendar.YEAR));
            time.setMonth((byte) calendar.get(Calendar.MONTH));
            time.setDay((byte) (calendar.get(Calendar.DATE) - 1));

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
        List<TelegramResponse> responses = new ArrayList<>();
        if(update.hasCallbackQuery()){
            responses.add(
                    new TelegramResponse(new AnswerCallbackQuery()
                            .setCallbackQueryId(update.getCallbackQuery().getId()))
            );
        }

        user.popStatus();
        user.pushStatus(timeHourLogicComponent.getType());
        user = userService.save(user);

        incident.setTime(time);
        incident = incidentService.save(incident);

        responses.addAll(super.getHandlerByStatus(user.peekStatus()).getMessage(user, update));
        return responses;
    }
}
