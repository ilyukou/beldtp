package org.telegram.bot.beldtp.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;

import java.util.List;

public interface StorageJpaRepository extends JpaRepository<Storage, Long> {
    Storage findByType(StorageType type);
}
