package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.repository.interf.IncidentRepository;
import org.telegram.bot.beldtp.repository.jpa.IncidentJpaRepository;

import java.util.List;

@Service
public class IncidentRepositoryImpl implements IncidentRepository {

    @Autowired
    private IncidentJpaRepository incidentJpaRepository;

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
    public Incident save(Incident incident) {
        return incidentJpaRepository.save(incident);
    }

    @Override
    public void delete(Incident incident) {
        incidentJpaRepository.delete(incident);
    }
}
