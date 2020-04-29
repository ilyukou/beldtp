package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeNowHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeSelectHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeTodayHandler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.TimeYesterdayHandler;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
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

    @Autowired
    private BackHandler backHandler;

    @Override
    public String getLabel(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getTime() != null
                && draft.getTime().getMinute() != null
                && draft.getTime().getHour() != null
                && draft.getTime().getDay() != null
                && draft.getTime().getMonth() != null
                && draft.getTime().getYear() != null
        ) {
            StringBuilder builder = new StringBuilder();

            if(draft.getTime().getHour() < 10){
                builder.append("0").append(draft.getTime().getHour());
            }else {
                builder.append(draft.getTime().getHour());
            }

            builder.append(":");

            if(draft.getTime().getMinute() < 10){
                builder.append("0").append(draft.getTime().getMinute());
            }else {
                builder.append(draft.getTime().getMinute());
            }

            builder.append("  ");
            builder.append(draft.getTime().getDay());
            builder.append("/");
            builder.append(draft.getTime().getMonth()+1);
            builder.append("/");
            builder.append(draft.getTime().getYear());

            return EmojiUtil.CHECK_MARK_BUTTON + " " + builder;

        }

        return EmojiUtil.WHITE_LARGE_SQUARE + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(timeNowHandler,
                timeTodayHandler,
                timeYesterdayHandler,
                timeSelectHandler,
                backHandler);
    }
}
