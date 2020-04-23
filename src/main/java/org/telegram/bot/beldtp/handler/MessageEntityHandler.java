package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.subclasses.LanguageHandler;
import org.telegram.bot.beldtp.handler.subclasses.StartHandler;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@HandlerInfo(type = "test", accessRight = UserRole.USER)
public class MessageEntityHandler extends Handler {

    private static final int DEFAULT_REACT_COMMAND_ID_IN_LIST = 0;
    private static final String BOT_COMMAND_MESSAGE_ENTITY_TYPE = "bot_command";
    private static final String START_COMMAND = "/start";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private LanguageHandler languageHandler;

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {

        if (update.getMessage().getEntities().get(DEFAULT_REACT_COMMAND_ID_IN_LIST).getType()
                .equals(BOT_COMMAND_MESSAGE_ENTITY_TYPE)) {
            return getMessageToCommand(update.getMessage().getEntities().get(DEFAULT_REACT_COMMAND_ID_IN_LIST),
                    user, update);
        }

        return null;
    }

    private List<TelegramResponse> getMessageToCommand(MessageEntity messageEntity,
                                                       User user, Update update) {
        List<String> strings = new ArrayList<>();

        if (user.getStatus() != null) {
            strings = new ArrayList<>(user.getStatus());
        }

        // user in startHandler
        if (strings.size() == 1 && strings.get(0).equals(startHandler.getType())) {
            return null;
        }

        // bot wait language from user. Not access to restart bot
        if (strings.size() == 2 && strings.get(0).equals(startHandler.getType())
                && strings.get(1).equals(languageHandler.getType())) {
            return null;
        }

        if (messageEntity.getText().equals(START_COMMAND)) {
            Stack<String> status = new Stack<>();
            status.push(startHandler.getType());

            user.setStatus(status);
            user = userService.save(user);

            Incident draft = incidentService.getDraft(user);
            if (draft != null) {
                incidentService.delete(draft);
            }

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        return null;
    }

    public boolean isUpdateHasMessageEntity(Update update){
        return update.hasMessage()
                && update.getMessage().hasEntities()
                && update.getMessage().getEntities().size() > 0;
    }
}
