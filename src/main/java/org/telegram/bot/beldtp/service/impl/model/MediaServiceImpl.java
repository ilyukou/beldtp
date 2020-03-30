package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Media;
import org.telegram.bot.beldtp.repository.interf.MediaRepository;
import org.telegram.bot.beldtp.service.interf.model.MediaService;

import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Override
    public Media save(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public List<Media> get(Long incidentId) {
        return mediaRepository.get(incidentId);
    }

    @Override
    public List<Media> getAll() {
        return mediaRepository.getAll();
    }
}
