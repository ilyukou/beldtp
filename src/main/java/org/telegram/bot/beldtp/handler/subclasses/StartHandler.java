package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Stack;

@HandlerInfo(type = "start", accessRight = UserRole.USER)
public class StartHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private LanguageHandler languageHandler;

    @Autowired
    private MainHandler mainHandler;

    private List<TelegramResponse> addStartMessage(List<TelegramResponse> responses, User user, Update update){
        Language language = Language.BE;

        // If user has language set user language
        if (user != null && user.getLanguage() != null){
            language = user.getLanguage();
        }

        responses.add(
                new TelegramResponse(
                        new SendMessage()
                                .setChatId(UpdateUtil.getChatId(update))
                                .setText(getAnswer(language).getText())
                )
        );

        return responses;
    }

    @Override
    public List<TelegramResponse> getMessage(List<TelegramResponse> responses, User user, Update update) {
        // User is complete all registration
        if(user != null && user.getLanguage() != null && user.getStatus() != null){
            return handle(responses, user,update);
        }

        responses = addStartMessage(responses, user,update);

        Stack<String> stack = new Stack<>();
        stack.push(getType());

        // User not exist
        if(user == null || user.getId() == null || !userService.isExist(user.getId())){
            stack.push(languageHandler.getType());
            user = new User(update);

        } else {
            // User exist
            stack.push(mainHandler.getType());
        }

        user.setStatus(stack);
        user = userService.save(user);
        return super.getHandlerByStatus(user.peekStatus()).getMessage(responses,user,update);
    }

    @Override
    public List<TelegramResponse> handle(List<TelegramResponse> responses, User user, Update update) {

        if (user.getLanguage() != null){
            user.pushStatus(mainHandler.getType());
//            user.setRole(UserRole.USER);
            user.setRole(UserRole.ADMIN);

        } else {
            Stack<String> status = new Stack<>();

            status.push(getType());
            status.push(languageHandler.getType());
            user.setStatus(status);
        }

        user = userService.save(user);
        return super.getHandlerByStatus(user.peekStatus()).getMessage(responses, user,update);
    }
}
