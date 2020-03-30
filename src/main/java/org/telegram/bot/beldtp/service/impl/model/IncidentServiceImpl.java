package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.repository.interf.IncidentRepository;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.MediaService;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;

import java.util.*;

@Service
public class IncidentServiceImpl implements IncidentService {

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
}
