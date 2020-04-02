package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackAndRejectIncidentHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@HandlerInfo(type = "addMedia", accessRight = UserRole.USER)
public class AddMediaHandler extends Handler {

    private static final String REQUIRED_MEDIA = "requiredMedia";
    private static final String ADDED_MEDIA = "addedMedia";

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Autowired
    private AddTextHandler addTextHandler;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse getMessage(User user, Update update) {

        Incident draft = incidentService.getDraft(user);

        if(draft.getMedia() == null || draft.getMedia().size() == 0){
            return getMessageWithoutAddTextHandlerButton(user,update);
        }

        return super.getMessage(user, update);
    }

    private TelegramResponse getMessageWithoutAddTextHandlerButton(User user, Update update){
        InlineKeyboardMarkup markupInline  = getInlineKeyboardMarkup(user);


        List<Handler> handlers = getChild();
        handlers.remove(addTextHandler);

        markupInline.setKeyboard(
                handlers.stream()
                        .filter(logicComponent ->
                                user.getRole().getValue() >= logicComponent.getAccessRight().getValue())
                        .collect(Collectors.toList())
                        .parallelStream()
                        .map(handler -> Collections.singletonList(new InlineKeyboardButton()
                                .setText(answerService.get(handler.getType(), user.getLanguage()).getLabel())
                                .setCallbackData(answerService.get(handler.getType(), user.getLanguage()).getType())))
                        .collect(Collectors.toList())
        );

        if(update.hasCallbackQuery()){
            EditMessageText editMessageText = new EditMessageText();

            editMessageText.setChatId(user.getId());
            editMessageText.setText(getAnswer(user.getLanguage()).getText());
            editMessageText.setReplyMarkup(markupInline);

            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            return new TelegramResponse(editMessageText,update);
        }

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getId());
        sendMessage.setText(getAnswer(user.getLanguage()).getText());
        sendMessage.setReplyMarkup(markupInline);

        return new TelegramResponse(sendMessage);
    }

    @Override
    public TelegramResponse handle(User user, Update update) {
        Incident draft =  incidentService.getDraft(user);

        TelegramResponse transaction = transaction(user,update);

        if(transaction != null){
            return transaction;
        }

        if(update.hasMessage() && (update.getMessage().hasVideo() || update.getMessage().hasPhoto())){
            Media media = new Media();

            if(update.getMessage().hasVideo()){
                Video video = update.getMessage().getVideo();
                media.setFileId(video.getFileId());
                media.setMediaType(MediaType.VIDEO);
            }

            if(update.getMessage().hasPhoto()){
                List<PhotoSize> list = update.getMessage().getPhoto();

                if(list.size() == 0){
                    return getMessageWhenMediaHasNotInUpdate(user,update);
                }

                PhotoSize photo = list.get(list.size() - 1); // last photo is best resolution photo
                media.setFileId(photo.getFileId());
                media.setMediaType(MediaType.PHOTO);

            }

            media.setCaption(update.getMessage().getCaption());

            media.setIncident(draft);
            draft.add(media);

            media = mediaService.save(media);
            draft = incidentService.save(draft);

            user = userService.save(user);

            return getMessageWhenMediaAdd(user,update);
        }

        return getMessageWhenMediaHasNotInUpdate(user,update);
    }

    private TelegramResponse getMessageWhenMediaAdd(User user, Update update){
        telegramResponseBlockingQueue
                .push(
                        new TelegramResponse(
                                new SendMessage()
                                        .setText("Media add")
                                        .setChatId(user.getId())
                        )
                );

        return super.getMessage(user, update);
    }

    private TelegramResponse getMessageWhenMediaHasNotInUpdate(User user, Update update) {
        telegramResponseBlockingQueue
                .push(
                        new TelegramResponse(
                                new SendMessage()
                                        .setText("Error. You send not media")
                                        .setChatId(user.getId())
                        )
                );

        return super.getMessage(user, update);
    }

    @Override
    public TelegramResponse transaction(User user, Update update) {
        if(update.hasCallbackQuery()){
            String callback = update.getCallbackQuery().getData();

            if(callback.equals(backAndRejectIncidentHandler.getType())){
                user.pushStatus(backAndRejectIncidentHandler.getType());

                user = userService.save(user);

                return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
            }

            if(callback.equals(addTextHandler.getType())){

                Incident draft = incidentService.getDraft(user);

                if (draft.getMedia() != null && draft.getMedia().size() > 0){
                    user.pushStatus(addTextHandler.getType());

                    user = userService.save(user);

                    return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
                } else {
                    return new TelegramResponse(
                                            new AnswerCallbackQuery()
                                                    .setText("Required one media")
                                                    .setCallbackQueryId(update.getCallbackQuery().getId())
                                    );
                }
            }

        }

        return null;
    }

    @Override
    public List<Handler> getChild() {
        List<Handler> list = new LinkedList<>();
        list.add(addTextHandler);
        list.add(backAndRejectIncidentHandler);
        return list;
    }
}
