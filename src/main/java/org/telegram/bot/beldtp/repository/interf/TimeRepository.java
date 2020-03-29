package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.Time;

public interface TimeRepository {
    Time get(Incident incident);

    Time save(Time time);

    void delete(Time time);
}
