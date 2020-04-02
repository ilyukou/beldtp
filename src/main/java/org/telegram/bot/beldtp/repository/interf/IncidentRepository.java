package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;

import java.util.List;

public interface IncidentRepository {
    List<Incident> get(User user, IncidentType incidentType);

    List<Incident> get(IncidentType incidentType);

    List<Incident> get(User user);

    Incident get(Long id);

    Incident save(Incident incident);

    List<Incident> save(List<Incident> incidents);

    void delete(Incident incident);

    long count();

    Incident getDraft(User user);

    long size(IncidentType incidentType);
}
