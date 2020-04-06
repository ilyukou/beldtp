package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.exception.MediaSizeException;
import org.telegram.bot.beldtp.exception.TextSizeException;
import org.telegram.bot.beldtp.handler.subclasses.StartHandler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    public TelegramResponse handle(Update update) {
        User user = userService.get(UpdateUtil.getChatId(update));

        TelegramResponse response;

        try {
            if (user == null) {
                response = startHandler.getMessage(null, update);
            } else {
                user = userService.update(user, update);
                response = startHandler
                        .getHandlerByStatus(user.peekStatus())
                        .handle(user, update);
            }
        } catch (BadRequestException | MediaSizeException | TextSizeException e) {
            response = null;

        } catch (Exception e) { // any Exception throw from Handlers
            response = exceptionHandler.getMessage(userService.get(UpdateUtil.getChatId(update)), update);
        }

        if (response == null) {
            response = exceptionHandler.getMessage(userService.get(UpdateUtil.getChatId(update)), update);
        }

        return response;
    }
}
