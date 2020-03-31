package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;
import org.telegram.bot.beldtp.repository.interf.ResourceRepository;
import org.telegram.bot.beldtp.repository.jpa.ResourceJpaRepository;
import org.telegram.bot.beldtp.service.interf.model.StorageService;

import java.util.LinkedList;
import java.util.List;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    @Autowired
    private ResourceJpaRepository resourceJpaRepository;

    @Autowired
    private StorageService storageService;

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

    @Override
    public Resource get(Long id) {
        return resourceJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Resource> get(StorageType storageType) {
        return new LinkedList<>(storageService.get(storageType).getResources());
    }

    @Override
    public boolean isExist(Resource resource) {
        return resource != null
                && resource.getId() != null
                && resourceJpaRepository.existsById(resource.getId());
    }
}
