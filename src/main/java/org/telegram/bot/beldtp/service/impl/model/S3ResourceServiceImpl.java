package org.telegram.bot.beldtp.service.impl.model;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Resource;
import org.telegram.bot.beldtp.model.Storage;
import org.telegram.bot.beldtp.model.StorageType;
import org.telegram.bot.beldtp.repository.interf.ResourceRepository;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;
import org.telegram.bot.beldtp.service.interf.model.StorageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class S3ResourceServiceImpl implements ResourcesService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private StorageService storageService;

    @Value("${s3.bucketName}")
    private String bucketName;

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
    public byte[] get(Resource resource) {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, resource.getFileName());

        try {
            S3Object objectPortion = amazonS3.getObject(rangeObjectRequest);
            return objectPortion.getObjectContent().readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Resource save(byte[] bytes, Resource resource)  {

        if(!resourceRepository.isExist(resource)){
            resource = resourceRepository.save(resource);
        }

        InputStream inputStream = new ByteArrayInputStream(bytes);

        amazonS3.putObject(bucketName, resource.getFileName(), inputStream, null);

        Storage storage = storageService.get(StorageType.S3);
        resource.setStorage(storage);

        try {
            inputStream.close();
        } catch (IOException e) {
            // ignore
        }

        return resource;
    }
}
