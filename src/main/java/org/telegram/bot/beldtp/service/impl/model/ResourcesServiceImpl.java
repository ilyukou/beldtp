package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;
import org.telegram.bot.beldtp.repository.interf.ResourceRepository;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;

import java.io.InputStream;
import java.util.List;

@Service
public class ResourcesServiceImpl implements ResourcesService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Resource get(Long id) {
        return resourceRepository.get(id);
    }

    @Override
    public List<Resource> get(StorageType storageType) {
        return resourceRepository.get(storageType);
    }

    @Override
    public List<Resource> get(Storage storage) {
        return resourceRepository.get(storage);
    }

    @Override
    public InputStream get(Resource resource) {
        // FIXME - add S3 impl for get inputStream
        return null;
    }
}
