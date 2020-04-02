package org.telegram.bot.beldtp.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Location;

public interface LocationJpaRepository extends JpaRepository<Location,Long> {
}
