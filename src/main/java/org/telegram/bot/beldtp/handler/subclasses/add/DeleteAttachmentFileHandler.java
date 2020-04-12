package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
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

import java.util.List;

@HandlerInfo(type = "deleteMedia", accessRight = UserRole.USER)
public class DeleteAttachmentFileHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private AnswerService answerService;

    @Override
    public String getLabel(User user, Update update) {
        return EmojiUtil.CROSS_MARK + " " + getAnswer(user.getLanguage()).getLabel();
    }

    @Override
    public List<TelegramResponse> getMessage(List<TelegramResponse> responses, User user, Update update) {
        Incident draft = incidentService.getDraft(user);
        int size = draft.getAttachmentFiles().size();
        draft.getAttachmentFiles().clear();
        draft = incidentService.save(draft);

        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }

        user = userService.save(user);

        if (update.hasCallbackQuery()) {
            responses.add(
                            new TelegramResponse(
                                    new AnswerCallbackQuery()
                                            .setCallbackQueryId(update.getCallbackQuery().getId())
                                            .setText(getText(user, update))));
        }

        if(size > 0){
            return super.getHandlerByStatus(user.peekStatus()).getMessage(responses, user, update);
        }else {
            return responses;
        }
    }
}
