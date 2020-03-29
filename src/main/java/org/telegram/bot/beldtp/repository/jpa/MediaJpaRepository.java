package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Media;

import java.util.List;

public interface MediaJpaRepository extends JpaRepository<Media, Long> {
    List<Media> findByIncident_Id(Long incidentId);
}
