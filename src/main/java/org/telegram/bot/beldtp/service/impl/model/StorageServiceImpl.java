package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;
import org.telegram.bot.beldtp.repository.interf.StorageRepository;
import org.telegram.bot.beldtp.service.interf.model.StorageService;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageRepository storageRepository;

    @Override
    public Storage get(StorageType storageType) {
        return storageRepository.get(storageType);
    }

    @Override
    public boolean isExist(StorageType type) {
        return storageRepository.isExist(type);
    }
}
