package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface ResourcesService {

    Resource save(Resource resource);

    Resource get(Long id);

    List<Resource> get(StorageType storageType);

    List<Resource> get(Storage storage);

    byte[] get(Resource resource);

    Resource save(byte[] bytes, Resource resource);
}
