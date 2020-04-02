package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.IncidentRepository;
import org.telegram.bot.beldtp.repository.jpa.IncidentJpaRepository;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncidentRepositoryImpl.class);

    @Autowired
    private IncidentJpaRepository incidentJpaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourcesService resourcesService;

    @Override
    public List<Incident> get(User user, IncidentType incidentType) {
        return incidentJpaRepository.findByUserAndType(user, incidentType);
    }

    @Override
    public List<Incident> get(IncidentType incidentType) {
        return incidentJpaRepository.findByType(incidentType);
    }

    @Override
    public List<Incident> get(User user) {
        return incidentJpaRepository.findByUser(user);
    }

    @Override
    public Incident get(Long id) {
        return incidentJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Incident save(Incident incident) {
        return incidentJpaRepository.save(incident);
    }

    @Override
    public List<Incident> save(List<Incident> incidents) {
        return incidentJpaRepository.saveAll(incidents);
    }

    @Override
    public void delete(Incident incident) {
        incidentJpaRepository.delete(incident);
    }

    @Override
    public long count() {
        return incidentJpaRepository.count();
    }

    @Override
    public Incident getDraft(User user) {
        List<Incident> drafts = incidentJpaRepository
                .findByUserAndType(user, IncidentType.DRAFT);

        if(drafts == null || drafts.size() == 0) {
            return createDraft(user);
        }

        if(drafts.size() == 1){
            return drafts.get(0);
        }

        LOGGER.warn("Found more than one draft for user. user id : " + user.getId());
        return drafts.get(0);
    }

    @Override
    public long size(IncidentType incidentType) {
        return get(incidentType).size();
    }

    private Incident createDraft(User user) {
        Incident incident = new Incident();
        incident.setType(IncidentType.DRAFT);
        incident.setUser(user);

        incident = save(incident);
        user.add(incident);
        user = userService.save(user);

        return incident;
    }
}
