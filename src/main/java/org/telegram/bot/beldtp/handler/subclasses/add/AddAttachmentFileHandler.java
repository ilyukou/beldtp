package org.telegram.bot.beldtp.handler.subclasses.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.AttachmentFileSizeException;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.BackHandler;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.bot.beldtp.util.AttachmentFileUtil;
import org.telegram.bot.beldtp.util.EmojiUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "addAttachmentFile", accessRight = UserRole.USER)
public class AddAttachmentFileHandler extends Handler {

    private static final String MEDIA_WAS_ADDED = "attachmentFileWasAdded";
    private static final String NO_SPACE_FOR_NEW_MEDIA = "noSpaceForNewMedia";
    private static final String FILE_EXTENSION = "fileExtension";
    private static final String FILE_SIZE = "maxFileSize";

    @Value("${beldtp.file.max-size}")
    private Integer fileMaxSize;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private DeleteAttachmentFileHandler deleteAttachmentFileHandler;

    @Autowired
    private AttachmentFileUtil attachmentFileUtil;

    @Value("${beldtp.incident.max-attachmentFiles-count}")
    private Integer maxMediaSize;

    @Override
    public List<TelegramResponse> getMessage(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getAttachmentFiles() != null && draft.getAttachmentFiles().size() > maxMediaSize) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
            }
            user = userService.save(user);

            List<TelegramResponse> responses = new ArrayList<>();
            if (update.hasCallbackQuery()) {
                responses.add(
                        new TelegramResponse(
                                new AnswerCallbackQuery()
                                        .setCallbackQueryId(update.getCallbackQuery().getId())
                                        .setText(answerService.get(NO_SPACE_FOR_NEW_MEDIA, user.getLanguage())
                                                .getText())));
            }


            responses.addAll(super.getHandlerByStatus(user.peekStatus()).getMessage(user, update));

            return responses;
        } else {
            return super.getMessage(user, update);
        }
    }

    @Override
    public String getLabel(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getAttachmentFiles() == null || draft.getAttachmentFiles().size() == 0) {
            return EmojiUtil.WHITE_LARGE_SQUARE + " " + getAnswer(user.getLanguage()).getLabel();

        }

        long photoSize = incidentService.getPhoto(draft).size();
        long videoSize = incidentService.getVideo(draft).size();

        long fileSize = incidentService.getNotMediaAttachmentFile(draft).size();

        StringBuilder builder = new StringBuilder();
        builder.append(EmojiUtil.CHECK_MARK_BUTTON);
        builder.append(" | ");

        if (photoSize > 0) {
            builder.append(" ").append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append(" | ");
        }

        if (videoSize > 0) {
            builder.append(" ").append(EmojiUtil.VIDEO_CAMERA).append(" ").append(videoSize).append(" | ");
        }

        if (fileSize > 0) {
            builder.append(" ").append(EmojiUtil.PAGE_FACING_UP).append(" ").append(fileSize).append(" | ");
        }

         builder.append(super.getLabel(user, update));

        return builder.toString();
    }

    @Override
    public String getText(User user, Update update) {
        Incident draft = incidentService.getDraft(user);

        if (draft.getAttachmentFiles() == null || draft.getAttachmentFiles().size() == 0) {
            return super.getText(user, update)
                    + "\n\n" + getFileExtension(user.getLanguage())
                    + "\n\n" + getFileSizeInfo(user.getLanguage());
        }

        long photoSize = incidentService.getPhoto(draft).size();
        long videoSize = incidentService.getVideo(draft).size();

        long fileSize = incidentService.getNotMediaAttachmentFile(draft).size();

        StringBuilder builder = new StringBuilder();

        if (photoSize > 0) {
            builder.append(EmojiUtil.FRAMED_PICTURE).append(" ").append(photoSize).append("\n");
        }

        if (videoSize > 0) {
            builder.append(EmojiUtil.VIDEO_CAMERA).append(" ").append(videoSize).append("\n");
        }

        if (fileSize > 0) {
            builder.append(EmojiUtil.PAGE_FACING_UP).append(" ").append(fileSize).append("\n");
        }

        builder.append("\n");
        builder.append(answerService.get(MEDIA_WAS_ADDED, user.getLanguage()).getText())
                .append("\n");

        builder.append(getFileExtension(user.getLanguage())).append("\n");
        builder.append(getFileSizeInfo(user.getLanguage()));
        return builder.toString();
    }

    private String getFileExtension(Language language){
        StringBuilder builder = new StringBuilder()
                .append(answerService.get(FILE_EXTENSION,language).getText())
                .append(" : ");

        AttachmentFileType[] types = AttachmentFileType.values();

        for (int i = 0; i < types.length; i++) {
            if(i != 0){
                builder.append(", ");
            }
            builder.append(types[i].getFileExtension());
        }

        return builder.toString();
    }

    private String getFileSizeInfo(Language language){
        return answerService.get(FILE_SIZE,language).getText() + " " + (fileMaxSize / (1024 * 1024)) + " MB";
    }

    @Override
    public List<TelegramResponse> handle(User user, Update update) {

        List<TelegramResponse> transaction = super.transaction(user, update);

        if (transaction != null) {
            return transaction;
        }

        Incident draft = incidentService.getDraft(user);

        if(draft.getAttachmentFiles().size() >= maxMediaSize){
            throw new AttachmentFileSizeException();
        }

        if (!isValid(update, draft)) {
            throw new BadRequestException();
        }

        AttachmentFile attachmentFile = attachmentFileUtil.getFromUpdate(update);

        attachmentFile.setCaption(update.getMessage().getCaption());

        attachmentFile.setIncident(draft);
        draft.add(attachmentFile);

        attachmentFile = attachmentFileService.save(attachmentFile);
        draft = incidentService.save(draft);

        if (draft.getAttachmentFiles().size() == maxMediaSize) {
            if (user.peekStatus().equals(getType())) {
                user.popStatus();
            }
        }

        user = userService.save(user);

        return getMessage(user, update);
    }

    private boolean isValid(Update update, Incident draft) {
        if (draft.getAttachmentFiles() == null || draft.getAttachmentFiles().size() < maxMediaSize) {
            return true;
        }

        return draft.getAttachmentFiles().size() != maxMediaSize
                || !update.hasMessage()
                || (!update.getMessage().hasPhoto() && !update.getMessage().hasVideo());
    }

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(deleteAttachmentFileHandler, backHandler);
    }
}