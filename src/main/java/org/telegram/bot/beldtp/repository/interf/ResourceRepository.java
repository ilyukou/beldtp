package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;

import java.util.List;

public interface ResourceRepository {
    List<Resource> get(Storage storage);

    Resource save(Resource resource);

    void delete(Resource resource);

    Resource get(Long id);

    List<Resource> get(StorageType storageType);

    boolean isExist(Resource resource);
}
