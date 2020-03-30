package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.User;
import java.util.List;

public interface IncidentService {

    Incident save(Incident incident);

    List<Incident> save(List<Incident> incidents);

    void delete(Incident incident);

    Incident get(Long id);

    List<Incident> get(User user);

    Incident getDraft(User user);
}
