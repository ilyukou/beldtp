package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.Time;
import org.telegram.bot.beldtp.repository.interf.TimeRepository;
import org.telegram.bot.beldtp.repository.jpa.TimeJpaRepository;

@Repository
public class TimeRepositoryImpl implements TimeRepository {

    @Autowired
    private TimeJpaRepository timeJpaRepository;

    @Override
    public Time get(Incident incident) {
        return timeJpaRepository.findByIncident(incident);
    }

    @Override
    public Time save(Time time) {
        return timeJpaRepository.save(time);
    }

    @Override
    public void delete(Time time) {
        timeJpaRepository.delete(time);
    }

    @Override
    public Time get(Long id) {
        return timeJpaRepository.findById(id).orElse(null);
    }
}
