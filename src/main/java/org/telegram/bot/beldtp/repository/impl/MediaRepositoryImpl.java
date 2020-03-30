package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Media;
import org.telegram.bot.beldtp.repository.interf.MediaRepository;
import org.telegram.bot.beldtp.repository.jpa.MediaJpaRepository;

import java.util.List;

@Repository
public class MediaRepositoryImpl implements MediaRepository {

    @Autowired
    private MediaJpaRepository mediaJpaRepository;

    @Override
    public List<Media> get(Long incidentId) {
        return mediaJpaRepository.findByIncident_Id(incidentId);
    }

    @Override
    public Media save(Media media) {
        return mediaJpaRepository.save(media);
    }

    @Override
    public void delete(Media media) {
        mediaJpaRepository.delete(media);
    }

    @Override
    public List<Media> getAll() {
        return mediaJpaRepository.findAll();
    }
}
