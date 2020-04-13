package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.exception.AttachmentFileSizeException;
import org.telegram.bot.beldtp.exception.TextSizeException;
import org.telegram.bot.beldtp.handler.subclasses.StartHandler;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Component
public class UpdateHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private MessageEntityHandler messageEntityHandler;

    @Transactional
    public List<TelegramResponse> handle(Update update) {
        User user = userService.get(UpdateUtil.getChatId(update));

        List<TelegramResponse> response = null;

        try {
            // Get message for new user
            if (user == null ) {
                response = startHandler.getMessage(null, update);

            } else {
                // Not new user and update has entity
                if(messageEntityHandler.isUpdateHasMessageEntity(update)){
                    response = messageEntityHandler.getMessage(user,update);
                }

                // Not new user and update hasn't entity
                if(response == null){
                    user = userService.update(user, update);
                    response = startHandler
                            .getHandlerByStatus(user.peekStatus()) // Get handler by User Status
                            .handle(user, update);
                }
            }
        } catch (BadRequestException | AttachmentFileSizeException | TextSizeException e) {
            response = null;

        } catch (Exception e) { // any other Exception throw from Handlers
            response = null;
        }

        if (response == null) {
            response = exceptionHandler.getMessage(userService.get(UpdateUtil.getChatId(update)), update);
        }

        return response;
    }
}
