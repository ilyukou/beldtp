package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.Time;
import org.telegram.bot.beldtp.repository.interf.TimeRepository;
import org.telegram.bot.beldtp.service.interf.model.TimeService;

@Service
public class TimeServiceImpl implements TimeService {

    @Autowired
    private TimeRepository timeRepository;

    @Override
    public Time save(Time time) {
        return timeRepository.save(time);
    }

    @Override
    public Time get(Long id) {
        return timeRepository.get(id);
    }

    @Override
    public Time get(Incident incident) {
        return timeRepository.get(incident);
    }
}
