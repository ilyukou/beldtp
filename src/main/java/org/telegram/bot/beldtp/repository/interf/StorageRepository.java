package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;

import java.util.List;

public interface StorageRepository {

    List<Storage> get(StorageType type);

    Storage getOne(StorageType type);

    Storage save(Storage storage);

    void delete(Storage storage);

    boolean isExist(StorageType type);
}
