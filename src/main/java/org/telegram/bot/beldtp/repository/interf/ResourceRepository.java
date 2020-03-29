package org.telegram.bot.beldtp.repository.interf;


import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;

import java.util.List;

public interface ResourceRepository {
    List<Resource> get(Storage storage);

    Resource save(Resource resource);

    void delete(Resource resource);
}
