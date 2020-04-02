package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addText", accessRight = UserRole.USER)
public class AddTextHandler extends Handler {

    private static final String REQUIRED_TEXT = "requiredText";

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AddLocationHandler addLocationHandler;

    @Override
    public TelegramResponse handle(User user, Update update) {

        TelegramResponse transaction = transaction(user,update);

        if(transaction != null){
            return transaction;
        }

        if(update.hasMessage() && update.getMessage().hasText()){
            Incident draft = incidentService.getDraft(user);

            draft.setText(update.getMessage().getText());

            if(user.peekStatus().equals(getType())){
                user.popStatus();
            }

            user.pushStatus(addLocationHandler.getType());

            draft = incidentService.save(draft);
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        return getMessageWhenMediaHasNotText(user.getLanguage(),update);
    }

    private TelegramResponse getMessageWhenMediaHasNotText(Language language, Update update) {
        return new TelegramResponse(
                new AnswerCallbackQuery()
                        .setText(answerService.get(REQUIRED_TEXT,language).getText())
                        .setCallbackQueryId(update.getCallbackQuery().getId())
        );
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler,backAndRejectIncidentHandler);
    }

    @Override
    public TelegramResponse transaction(User user, Update update) {
        if(update.hasCallbackQuery()){
            String callback = update.getCallbackQuery().getData();

            if(callback.equals(addLocationHandler.getType())){

                Incident draft = incidentService.getDraft(user);

                if (draft.getText() != null && draft.getText().length() > 0){
                    user.pushStatus(addLocationHandler.getType());

                    user = userService.save(user);

                    return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
                } else {
                    return new TelegramResponse(
                            new AnswerCallbackQuery()
                                    .setText("Required description")
                                    .setCallbackQueryId(update.getCallbackQuery().getId())
                    );

                }
            } else {
                return super.transaction(user, update);
            }
        }

        return null;
    }
}
