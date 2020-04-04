package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.Arrays;
import java.util.List;

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
    private BackHandler backHandler;

    @Autowired
    private TelegramResponseBlockingQueue telegramResponseBlockingQueue;

    @Override
    public TelegramResponse handle(User user, Update update) {

        TelegramResponse transaction = super.transaction(user, update);

        if (transaction != null) {
            return transaction;
        }

        Incident draft = incidentService.getDraft(user);

        if (update.hasMessage() && (update.getMessage().hasVideo() || update.getMessage().hasPhoto())) {
            Media media = new Media();

            if (update.getMessage().hasVideo()) {
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
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }
}
