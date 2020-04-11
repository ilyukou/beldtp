package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Media;

import java.util.List;

public interface MediaService {
    Media save(Media media);

    List<Media> get(Long incidentId);

    List<Media> getAll();

    void delete(Media media);
}
