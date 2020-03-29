package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;
import org.telegram.bot.beldtp.repository.interf.StorageRepository;
import org.telegram.bot.beldtp.repository.jpa.StorageJpaRepository;

import java.util.List;

@Service
public class StorageRepositoryImpl implements StorageRepository {

    @Autowired
    private StorageJpaRepository storageJpaRepository;

    @Override
    public List<Storage> get(StorageType type) {
        return storageJpaRepository.findStorageByType(type);
    }

    @Override
    public Storage save(Storage storage) {
        return storageJpaRepository.save(storage);
    }

    @Override
    public void delete(Storage storage) {
        storageJpaRepository.delete(storage);
    }
}