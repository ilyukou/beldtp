package org.telegram.bot.beldtp.handler.subclasses.add.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.time.timeSelect.YearTimeHandler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@HandlerInfo(type = "timeSelect", accessRight = UserRole.USER)
public class TimeSelectHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private YearTimeHandler timeYearLogicComponent;

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {

        if(getType().equals(user.peekStatus())){
            user.popStatus();
        }

        user.pushStatus(timeYearLogicComponent.getType());
        user = userService.save(user);

        return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
