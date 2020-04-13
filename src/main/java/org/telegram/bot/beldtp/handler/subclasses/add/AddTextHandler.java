package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.TextSizeException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addText", accessRight = UserRole.USER)
public class AddTextHandler extends Handler {

    private static final String REQUIRED_TEXT = "requiredText";

    private static final String TEXT_WAS_ADDED = "textWasAdded";

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private AnswerService answerService;

    @Value("${beldtp.incident.max-text-size}")
    private Integer maxTextSize;

    @Override
    public String getText(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getText() != null) {
            return answerService.get(TEXT_WAS_ADDED, user.getLanguage()).getText()
                    + "\n\n" + "_" + draft.getText() + "_";
        }

        return getAnswer(user.getLanguage()).getText();
    }

    @Override
    public String getLabel(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getText() != null) {
            return EmojiUtil.CHECK_MARK_BUTTON + " " + getAnswer(user.getLanguage()).getLabel();
        }

        return EmojiUtil.WHITE_LARGE_SQUARE + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {

        List<TelegramResponse> transaction = transaction(user, update);

        if (transaction != null) {
            return transaction;
        }

        if(update.hasMessage() && update.getMessage().hasText()) {

            if (!isValid(update)) {
                throw new TextSizeException();
            }

            Incident draft = incidentService.getDraft(user);

            draft.setText(update.getMessage().getText());

            draft = incidentService.save(draft);
            user = userService.save(user);

            return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
        }

        return Arrays.asList(new TelegramResponse(
                new AnswerCallbackQuery()
                        .setText(answerService.get(REQUIRED_TEXT, user.getLanguage()).getText())
                        .setCallbackQueryId(update.getCallbackQuery().getId())));
    }

    private boolean isValid(Update update) {
        String text = update.getMessage().getText();

        return text.length() <= maxTextSize;
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }
}
