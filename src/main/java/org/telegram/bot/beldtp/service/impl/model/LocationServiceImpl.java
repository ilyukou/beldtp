package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Location;
import org.telegram.bot.beldtp.repository.interf.LocationRepository;
import org.telegram.bot.beldtp.service.interf.model.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public void delete(Location location) {
        locationRepository.delete(location);
    }
}
