package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.exception.TextSizeException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@HandlerInfo(type = "addText", accessRight = UserRole.USER)
public class AddTextHandler extends Handler {

    private static final String REQUIRED_TEXT = "requiredText";

    private static final String TEXT_WAS_ADDED = "textWasAdded";

    private static final String TEXT_HAS_ENTITIES = "textHasEntities";

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

        StringBuilder text = new StringBuilder();

        if(draft.getText() != null){
            text.append(answerService.get(TEXT_WAS_ADDED, user.getLanguage()).getText())
                    .append("\n\n")
                    .append(draft.getText());
        } else {
            text.append(getAnswer(user.getLanguage()).getText());
        }

        return text.toString();
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

            List<TelegramResponse> responses = new ArrayList<>();

            Incident draft = incidentService.getDraft(user);

            if(update.getMessage().hasEntities()){

                if(update.getMessage().getEntities().get(0).getType().equals("url")){
                    draft.setText(wrapUrlInMarkdown(draft, update));

                } else {
                    SendMessage message = new SendMessage();
                    message.setText(answerService.get(TEXT_HAS_ENTITIES, user.getLanguage()).getText());
                    message.setChatId(user.getId());
                    message.setParseMode(super.getParseMode(user,update));

                    responses.add(new TelegramResponse(message));
                }

            } else {

                draft.setText(update.getMessage().getText());

                draft = incidentService.save(draft);
                user = userService.save(user);
            }

            responses.addAll(super.getHandlerByStatus(user.peekStatus()).getMessage(user, update));
            return responses;
        }

        throw new BadRequestException();
    }

    private boolean isValid(Update update) {
        String text = update.getMessage().getText();

        return text.length() <= maxTextSize;
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }

    private String wrapUrlInMarkdown(Incident incident, Update update){
        String text = update.getMessage().getText();

        List<String> listUrl = update.getMessage().getEntities()
                .stream()
                .map(MessageEntity::getText)
                .collect(Collectors.toList());

        for (String string : listUrl){
//            text = text.replaceFirst(string, "[" + string + "](" + string + ")");
            text = text.replaceFirst(string, "<a href=\"" + string + "\">" + string + "</a>");
        }

        return text;
    }
}
