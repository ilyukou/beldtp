package org.telegram.bot.beldtp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.BeldtpBot;
import org.telegram.bot.beldtp.exception.AttachmentFileSizeException;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.exception.TextSizeException;
import org.telegram.bot.beldtp.handler.subclasses.StartHandler;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Stack;

@Component
public class UpdateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private MessageEntityHandler messageEntityHandler;

    @Autowired
    private BeldtpBot bot;

    @Transactional
    public void handle(Update update) {
        User user = userService.get(UpdateUtil.getChatId(update));

        List<TelegramResponse> response = null;

        try {
            // Get message for new user
            if (user == null) {
                response = startHandler.getMessage(null, update);

            } else {

                // Default Language
                if (user.getLanguage() == null) {
                    user.setLanguage(Language.BE);
                    user = userService.save(user);
                }

                // Default Handler
                if (user.getStatus() == null || user.getStatus().size() == 0) {
                    Stack<String> status = new Stack<>();
                    status.push(startHandler.getType());

                    user.setStatus(status);
                    user = userService.save(user);
                }

                // Not new user and update has entity
                if (messageEntityHandler.isUpdateHasMessageEntity(update)) {
                    response = messageEntityHandler.getMessage(user, update);
                }

                // Not new user and update hasn't entity
                if (response == null) {
                    user = userService.update(user, update);
                    response = startHandler
                            .getHandlerByStatus(user.peekStatus()) // Get handler by User Status
                            .handle(user, update);
                }
            }
        } catch (BadRequestException | AttachmentFileSizeException | TextSizeException e) {
            response = null;

        } catch (NullPointerException e) { // any other Exception throw from Handlers
            LOGGER.error("NullPointerException in " + update.toString() + " ; from " + user.toString(), e);
            response = null;
        } catch (Exception e) { // any other Exception throw from Handlers
            LOGGER.error("Exception in " + update.toString() + " ; from " + user.toString(), e);
            response = null;
        }

        if (response == null) {
            response = exceptionHandler.getMessage(userService.get(UpdateUtil.getChatId(update)), update);
        }

        boolean result = execute(response);

        if (!result) {
            getFalseResultResponse(user,update);
        }
    }

    private boolean execute(List<TelegramResponse> response) {

        try {
            for (TelegramResponse telegramResponse : response) {
                bot.executeTelegramResponse(telegramResponse);
            }

        } catch (TelegramApiException e) {
            LOGGER.warn("Failed to send message. " + response.toString(), e);
            return false;

        } catch (Exception e) {
            LOGGER.error("Unrelated error with Telegram API. " + response.toString(), e);
            return false;
        }

        return true;
    }

    private void getFalseResultResponse(User user, Update update) {
        List<TelegramResponse> response = exceptionHandler  // FIXME - add false result response
                .getMessage(userService.get(UpdateUtil.getChatId(update)), update);

        boolean result = execute(response);

        if (!result) {
            LOGGER.error("Uncorrect telegram update or not suggested message : "
                    + update.toString() + " ; from " + user.toString());
        }
    }
}
