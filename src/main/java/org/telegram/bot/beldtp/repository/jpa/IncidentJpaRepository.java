package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.User;

import java.util.List;

public interface IncidentJpaRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByUserAndType(User user, IncidentType incidentType);

    List<Incident> findByType(IncidentType incidentType);

    List<Incident> findByUser(User user);
}
