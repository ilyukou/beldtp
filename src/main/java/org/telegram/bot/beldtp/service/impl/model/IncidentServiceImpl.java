package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.IncidentRepository;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class IncidentServiceImpl implements IncidentService {

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

            for (Media media : incident.getMedia()){
                if(media.getCaption() != null){
                    text.append(media.getCaption()).append("\n");
                }
            }

            if(incident.getLocation() != null){
                text.append(incident.getLocation().toString());

            }

            list.forEach(inputMedia -> {
                inputMedia.setParseMode(ParseMode.MARKDOWN);
                inputMedia.setCaption(null);
            });

            list.get(0).setCaption(text.toString());

            sendMediaGroup.setMedia(list);
        }
        return sendMediaGroup;
    }

    private List<InputMedia> convert(Incident incident) {
        List<InputMedia> inputMediaList = new LinkedList<>();

        if (incident.getMedia() != null && incident.getMedia().size() > 0) {
            incident.getMedia()
                    .forEach(media -> {

                        if (media.getMediaType() == MediaType.PHOTO) {
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

                        if (media.getMediaType() == MediaType.VIDEO) {
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
                    });
        }
        return inputMediaList;
    }

    @Override
    public long size(IncidentType incidentType) {
        return incidentRepository.size(incidentType);
    }
}
