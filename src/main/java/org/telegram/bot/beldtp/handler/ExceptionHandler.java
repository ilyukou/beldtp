package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@HandlerInfo(type = "exception", accessRight = UserRole.USER)
public class ExceptionHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Override
    public TelegramResponse getMessage(User user, Update update) {
        if(user == null || user.getId() == null){
            return new TelegramResponse(new SendMessage()
                    .setText("Critical error")
                    .setChatId(Objects.requireNonNull(UpdateUtil.getChatId(update))));
        }

        if(user.peekStatus().equals(getType())){
            user.popStatus();
            userService.save(user);
        }

        if(user.getLanguage() == null){
            user.setLanguage(Language.BE);
            userService.save(user);
        }

        return new TelegramResponse(new SendMessage()
                .setChatId(user.getId())
                .setText(getAnswer(user.getLanguage()).getText()));
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        return null;
    }
}
