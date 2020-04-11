package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@HandlerInfo(type = "addMedia", accessRight = UserRole.USER)
public class AddMediaHandler extends Handler {

    private static final String MEDIA_WAS_ADDED = "mediaWasAdded";
    private static final String NO_SPACE_FOR_NEW_MEDIA = "noSpaceForNewMedia";

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
    private DeleteMediaHandler deleteMediaHandler;

    @Value("${beldtp.incident.max-media-count}")
    private Integer maxMediaSize;

    @Override
    public List<TelegramResponse> getMessage(List<TelegramResponse> responses, User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getMedia() != null && draft.getMedia().size() >= maxMediaSize) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
            }
            user = userService.save(user);

            if (update.hasCallbackQuery()) {
                responses.add(
                        new TelegramResponse(
                                new AnswerCallbackQuery()
                                        .setCallbackQueryId(update.getCallbackQuery().getId())
                                        .setText(answerService.get(NO_SPACE_FOR_NEW_MEDIA, user.getLanguage())
                                                .getText())));
            }


            return super.getHandlerByStatus(user.peekStatus()).getMessage(responses, user, update);

        } else {
            return super.getMessage(responses, user, update);
        }
    }

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
        builder.append(EmojiUtil.CHECK_MARK_BUTTON);
        builder.append(" | ");
        if (photoSize > 0 && videoSize > 0) {
            builder.append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append(" | ");
            builder.append(EmojiUtil.VIDEO_CAMERA).append(" ").append(videoSize).append(" | ");

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
        builder.append(answerService.get(MEDIA_WAS_ADDED, user.getLanguage()).getText());

        return builder.toString();
    }

    @Override
    public List<TelegramResponse> handle(List<TelegramResponse> responses, User user, Update update) {

        List<TelegramResponse> transaction = super.transaction(responses, user, update);

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

                return super.getHandlerByStatus(user.peekStatus()).getMessage(responses, user, update);
            }

            Media media = new Media();
            media.setUploadDate(Calendar.getInstance().getTimeInMillis());

            if (update.getMessage().hasVideo()) {
                Video video = update.getMessage().getVideo();
                media.setFileId(video.getFileId());
                media.setMediaType(MediaType.VIDEO);
            }

            if (update.getMessage().hasPhoto()) {
                List<PhotoSize> list = update.getMessage().getPhoto();

                if(list.size() == 0){
                    throw new BadRequestException();
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

            if (draft.getMedia().size() == maxMediaSize) {
                if (user.peekStatus().equals(getType())) {
                    user.popStatus();
                }
            }

            user = userService.save(user);

            return getMessage(responses, user, update);
        }

        throw new BadRequestException();
    }

    private boolean isValid(Update update, Incident draft) {
        if (draft.getMedia() == null || draft.getMedia().size() < maxMediaSize) {
            return true;
        }

        return draft.getMedia().size() != maxMediaSize
                || !update.hasMessage()
                || (!update.getMessage().hasPhoto() && !update.getMessage().hasVideo());
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(deleteMediaHandler, backHandler);
    }
}
