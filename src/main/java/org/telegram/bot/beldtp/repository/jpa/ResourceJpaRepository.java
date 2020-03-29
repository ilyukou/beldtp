package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;

import java.util.List;

public interface ResourceJpaRepository extends JpaRepository<Resource, Long> {
    List<Resource> findResourceByStorage(Storage storage);
}
