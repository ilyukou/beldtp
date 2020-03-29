package org.telegram.bot.beldtp.repository.interf;


import org.telegram.bot.beldtp.model.Media;

import java.util.List;

public interface MediaRepository {
    List<Media> get(Long incidentId);

    Media save(Media media);

    void delete(Media media);
}
