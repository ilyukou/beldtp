package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.Time;

public interface TimeService {
    Time save(Time time);

    Time get(Long id);

    Time get(Incident incident);
}
