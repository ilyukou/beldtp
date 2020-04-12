package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.AttachmentFile;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;

import javax.print.attribute.standard.Media;
import java.util.List;

public interface IncidentService {

    Incident save(Incident incident);

    List<Incident> save(List<Incident> incidents);

    void delete(Incident incident);

    Incident get(Long id);

    List<Incident> get(User user);

    Incident getDraft(User user);

    List<Incident> get(IncidentType type);

    SendMediaGroup getSendMediaGroup(Incident incident);

    long size(IncidentType reject);

    List<AttachmentFile> getNotMediaAttachmentFile(Incident incident);

    List<AttachmentFile> getMedia(Incident incident);

    List<AttachmentFile> getPhoto(Incident incident);

    List<AttachmentFile> getVideo(Incident incident);
}
