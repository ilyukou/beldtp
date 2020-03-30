package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;

import java.util.List;

public interface StorageService {

    List<Storage> get(StorageType storageType);

    boolean isExist(StorageType type);

    Storage getOne(StorageType storageType);
}
