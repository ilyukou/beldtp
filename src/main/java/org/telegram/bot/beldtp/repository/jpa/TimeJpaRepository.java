package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.Time;

public interface TimeJpaRepository extends JpaRepository<Time, Long> {
    Time findByIncident(Incident incident);
}
