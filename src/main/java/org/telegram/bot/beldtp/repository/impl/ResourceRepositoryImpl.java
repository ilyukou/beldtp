package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.repository.interf.ResourceRepository;
import org.telegram.bot.beldtp.repository.jpa.ResourceJpaRepository;

import java.util.List;

@Service
public class ResourceRepositoryImpl implements ResourceRepository {

    @Autowired
    private ResourceJpaRepository resourceJpaRepository;

    @Override
    public List<Resource> get(Storage storage) {
        return resourceJpaRepository.findResourceByStorage(storage);
    }

    @Override
    public Resource save(Resource resource) {
        return resourceJpaRepository.save(resource);
    }

    @Override
    public void delete(Resource resource) {
        resourceJpaRepository.delete(resource);
    }
}
