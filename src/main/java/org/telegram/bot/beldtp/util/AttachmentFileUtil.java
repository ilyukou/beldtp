package org.telegram.bot.beldtp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.exception.AttachmentFileSizeException;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.model.AttachmentFile;
import org.telegram.bot.beldtp.model.AttachmentFileType;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.Calendar;
import java.util.List;

@Component
public class AttachmentFileUtil {

    @Value("${beldtp.file.max-size}")
    private Integer maxFileSize;

    public AttachmentFile getFromUpdate(Update update) throws BadRequestException, AttachmentFileSizeException {
        if(!update.hasMessage()){
            throw new BadRequestException();
        }

        AttachmentFile attachmentFile = new AttachmentFile();
        attachmentFile.setUploadDate(Calendar.getInstance().getTimeInMillis());

        if(update.getMessage().hasVideo()){
            return getVideo(attachmentFile, update.getMessage().getVideo());
        }

        if(update.getMessage().hasPhoto()){
            return getPhoto(attachmentFile, update.getMessage().getPhoto());
        }

        if(update.getMessage().hasDocument()){
            return getFile(attachmentFile, update.getMessage().getDocument());
        }

        throw new BadRequestException();
    }

    public AttachmentFile getPhoto(AttachmentFile attachmentFile, List<PhotoSize> listPhoto) throws BadRequestException {
        if(listPhoto.size() == 0){
            throw new BadRequestException();
        }

        PhotoSize photo = listPhoto.get(listPhoto.size() - 1); // last photo is best resolution photo
        attachmentFile.setFileId(photo.getFileId());
        attachmentFile.setAttachmentFileType(AttachmentFileType.PHOTO);

        return attachmentFile;
    }

    public AttachmentFile getVideo(AttachmentFile attachmentFile, Video video){

        attachmentFile.setFileId(video.getFileId());
        attachmentFile.setAttachmentFileType(AttachmentFileType.VIDEO);

        return attachmentFile;
    }

    public AttachmentFile getFile(AttachmentFile attachmentFile, Document document) throws BadRequestException, AttachmentFileSizeException{

        if(document.getFileSize() > maxFileSize){
            throw new AttachmentFileSizeException();
        }

        AttachmentFileType type = getFromDocumentAttachmentFileType(document);

        if(type == null){
            throw new BadRequestException();
        }

        attachmentFile.setFileId(document.getFileId());
        attachmentFile.setAttachmentFileType(type);

        return attachmentFile;
    }

    public static AttachmentFileType getFromDocumentAttachmentFileType(Document document){

        StringBuilder builder = new StringBuilder(document.getFileName());
        builder = builder.reverse();

        // [0] - file extension ; [1] - filename
        String[] arr = builder.toString().split("\\.");

        if(arr.length < 2){
            return null;
        }

        // gpj => jpg
        String fileExtension = new StringBuilder(arr[0]).reverse().toString();

        for (AttachmentFileType type : AttachmentFileType.values()){
            if(type.getFileExtension().equals(fileExtension) // check equals file extension
                && type.getMimeType().equals(document.getMimeType())){ // check equals mime type
                return type;
            }
        }

        return null;
    }
}
