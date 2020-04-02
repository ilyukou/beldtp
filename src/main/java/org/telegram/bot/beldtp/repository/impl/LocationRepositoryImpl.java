package org.telegram.bot.beldtp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Location;
import org.telegram.bot.beldtp.repository.interf.LocationRepository;
import org.telegram.bot.beldtp.repository.jpa.LocationJpaRepository;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    @Autowired
    private LocationJpaRepository locationJpaRepository;

    @Override
    public Location save(Location location) {
        return locationJpaRepository.save(location);
    }
}
