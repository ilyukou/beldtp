package org.telegram.bot.beldtp.repository.interf;


import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.User;

import java.util.List;

public interface IncidentRepository {
    List<Incident> get(User user, IncidentType incidentType);

    List<Incident> get(IncidentType incidentType);

    List<Incident> get(User user);

    Incident save(Incident incident);

    void delete(Incident incident);
}
