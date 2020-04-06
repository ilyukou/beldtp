package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.listener.telegramResponse.TelegramResponseBlockingQueue;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addMedia", accessRight = UserRole.USER)
public class AddMediaHandler extends Handler {

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

    @Value("${beldtp.incident.max-media-count}")
    private Integer maxMediaSize;

    @Override
    public String getLabel(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getMedia() == null || draft.getMedia().size() == 0) {
            return EmojiUtil.WHITE_LARGE_SQUARE + " " + getAnswer(user.getLanguage()).getLabel();
        }

        int photoSize = 0;
        int videoSize = 0;

        for (Media media : draft.getMedia()) {
            if (media.getMediaType().equals(MediaType.PHOTO)) {
                photoSize++;
            }
            if (media.getMediaType().equals(MediaType.VIDEO)) {
                videoSize++;
            }
        }

        StringBuilder builder = new StringBuilder();

        if (photoSize > 0 && videoSize > 0) {
            builder.append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append(" ");
            builder.append(EmojiUtil.VIDEO_CAMERA).append(" ").append(" ").append(videoSize).append(" | ");

        } else if (photoSize > 0) {
            builder.append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append(" | ");

        } else if (videoSize > 0) {
            builder.append(EmojiUtil.VIDEO_CAMERA).append(" ").append(videoSize).append(" | ");
        }

        builder.append(super.getLabel(user, update));

        return builder.toString();
    }

    @Override
    public String getText(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getMedia() == null || draft.getMedia().size() == 0) {
            return super.getText(user, update);
        }

        int photoSize = 0;
        int videoSize = 0;

        for (Media media : draft.getMedia()) {
            if (media.getMediaType().equals(MediaType.PHOTO)) {
                photoSize++;
            }
            if (media.getMediaType().equals(MediaType.VIDEO)) {
                videoSize++;
            }
        }

        StringBuilder builder = new StringBuilder();

        if (photoSize > 0) {
            builder.append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append("\n");
        }

        if (videoSize > 0) {
            builder.append(EmojiUtil.VIDEO_CAMERA).append(" ").append(videoSize).append("\n");
        }

        builder.append("\n");
        builder.append(super.getText(user, update));

        return builder.toString();
    }

    @Override
    public TelegramResponse handle(User user, Update update) {

        TelegramResponse transaction = super.transaction(user, update);

        if (transaction != null) {
            return transaction;
        }

        Incident draft = incidentService.getDraft(user);

        if (update.hasMessage() && (update.getMessage().hasVideo() || update.getMessage().hasPhoto())) {

            if (!isValid(update, draft)) {
                if (getType().equals(user.peekStatus())) {
                    user.popStatus();
                }

                user = userService.save(user);

//                telegramResponseBlockingQueue.push( FIXME
//                        new TelegramResponse(
//                                new SendMessage()
//                                        .setChatId(user.getId())
//                                        .setText("Media size is " + maxMediaSize)
//                        )
//                );

                return super.getHandlerByStatus(user.peekStatus()).getMessage(user, update);
            }

            Media media = new Media();

            if (update.getMessage().hasVideo()) {
                Video video = update.getMessage().getVideo();
                media.setFileId(video.getFileId());
                media.setMediaType(MediaType.VIDEO);
            }

            if (update.getMessage().hasPhoto()) {
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

            return getMessageWhenMediaAdd(user, update);
        }

        return getMessageWhenMediaHasNotInUpdate(user, update);
    }

    private boolean isValid(Update update, Incident draft) {
        if (draft.getMedia() == null || draft.getMedia().size() < maxMediaSize) {
            return true;
        }

        return draft.getMedia().size() < maxMediaSize;
    }

    private TelegramResponse getMessageWhenMediaAdd(User user, Update update) {
        telegramResponseBlockingQueue
                .push(
                        new TelegramResponse(
                                new SendMessage()
                                        .setText(answerService.get(ADDED_MEDIA, user.getLanguage()).getText())
                                        .setChatId(user.getId())
                        )
                );

        return super.getMessage(user, update);
    }

    private TelegramResponse getMessageWhenMediaHasNotInUpdate(User user, Update update) {
        throw new BadRequestException();
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(backHandler);
    }
}
