package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.IncidentRepository;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    private static final String LINK = "link";

    private static final Language DEFAULT_INCIDENT_LANGUAGE = Language.BE;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private IncidentRepository incidentRepository;

    @Override
    public Incident save(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public List<Incident> save(List<Incident> incidents) {
        return incidentRepository.save(incidents);
    }

    @Override
    public void delete(Incident incident) {
        incidentRepository.delete(incident);
    }

    @Override
    public Incident get(Long id) {
        return incidentRepository.get(id);
    }

    @Override
    public List<Incident> get(User user) {
        return incidentRepository.get(user);
    }

    @Override
    public Incident getDraft(User user) {
        return incidentRepository.getDraft(user);
    }

    @Override
    public List<Incident> get(IncidentType type) {
        return incidentRepository.get(type);
    }

    @Override
    public SendMediaGroup getSendMediaGroup(Incident incident) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();

        List<InputMedia> list = convert(incident);

        if (list.size() > 0) {

            StringBuilder text = new StringBuilder();

            if (incident.getText() != null && incident.getText().length() > 0) {
                text.append(incident.getText()).append("\n\n");
            }

            for (AttachmentFile attachmentFile : incident.getAttachmentFiles()){
                if(attachmentFile.getCaption() != null){
                    text.append(attachmentFile.getCaption()).append("\n");
                }
            }

            if(incident.getLocation() != null){
                text.append(incident.getLocation().toString());

            }

            list.forEach(inputMedia -> {
                inputMedia.setParseMode(ParseMode.MARKDOWN);
                inputMedia.setCaption(null);
            });

            text.append("\n").append("\n");
            text.append(incident.getTime().toString());

            list.get(0).setCaption(text.toString());

            sendMediaGroup.setMedia(list);
        }
        return sendMediaGroup;
    }

    private List<InputMedia> convert(Incident incident) {
        List<InputMedia> inputMediaList = new LinkedList<>();

        if (incident.getAttachmentFiles() != null && incident.getAttachmentFiles().size() > 0) {
            incident.getAttachmentFiles()
                    .forEach(media -> {

                        if (media.getAttachmentFileType() == AttachmentFileType.PHOTO
                                || media.getAttachmentFileType() == AttachmentFileType.PHOTO_JPG
                                || media.getAttachmentFileType() ==AttachmentFileType.PHOTO_PNG) {
                            InputMedia inputMedia = new InputMediaPhoto(null, media.getCaption());
                            InputStream stream = new ByteArrayInputStream(resourcesService.get(media.getResource()));
                            inputMedia.setMedia(stream, media.getResource().getFileName());
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            inputMediaList.add(inputMedia);
                        }

                        if (media.getAttachmentFileType() == AttachmentFileType.VIDEO) {
                            InputMedia inputMedia = new InputMediaVideo(null, media.getCaption());
                            InputStream stream = new ByteArrayInputStream(resourcesService.get(media.getResource()));
                            inputMedia.setMedia(stream, media.getResource().getFileName());
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            inputMediaList.add(inputMedia);
                        }
                        if (media.getAttachmentFileType() == AttachmentFileType.PDF
                                || media.getAttachmentFileType() == AttachmentFileType.DOCX) {
                            InputMedia inputMedia = new InputMediaDocument(null, media.getCaption());
                            InputStream stream = new ByteArrayInputStream(resourcesService.get(media.getResource()));
                            inputMedia.setMedia(stream, media.getResource().getFileName());
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            inputMediaList.add(inputMedia);
                        }
                    });
        }
        return inputMediaList;
    }

    @Override
    public long size(IncidentType incidentType) {
        return incidentRepository.size(incidentType);
    }

    @Override
    public List<AttachmentFile> getNotMediaAttachmentFile(Incident incident) {
        if(incident == null || incident.getAttachmentFiles() == null){
            return new LinkedList<>();
        }

        return incident.getAttachmentFiles().stream()
                .filter(attachmentFile
                        -> !(attachmentFile.getAttachmentFileType().equals(AttachmentFileType.PHOTO)
                        || attachmentFile.getAttachmentFileType().equals(AttachmentFileType.VIDEO)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentFile> getMedia(Incident incident) {
        if(incident == null || incident.getAttachmentFiles() == null){
            return new LinkedList<>();
        }

        return incident.getAttachmentFiles().stream()
                .filter(attachmentFile
                        -> attachmentFile.getAttachmentFileType().equals(AttachmentFileType.PHOTO)
                        || attachmentFile.getAttachmentFileType().equals(AttachmentFileType.VIDEO))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentFile> getPhoto(Incident incident) {
        if(incident == null || incident.getAttachmentFiles() == null){
            return new LinkedList<>();
        }

        return incident.getAttachmentFiles().stream()
                .filter(attachmentFile
                        -> attachmentFile.getAttachmentFileType().equals(AttachmentFileType.PHOTO))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentFile> getVideo(Incident incident) {
        if(incident == null || incident.getAttachmentFiles() == null){
            return new LinkedList<>();
        }

        return incident.getAttachmentFiles().stream()
                .filter(attachmentFile
                        -> attachmentFile.getAttachmentFileType().equals(AttachmentFileType.VIDEO))
                .collect(Collectors.toList());
    }
}
