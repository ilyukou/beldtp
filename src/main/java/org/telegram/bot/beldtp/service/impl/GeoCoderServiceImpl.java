package org.telegram.bot.beldtp.service.impl;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageComponents;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageResult;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Location;
import org.telegram.bot.beldtp.service.interf.GeoCoderService;

@Service
public class GeoCoderServiceImpl implements GeoCoderService {

    private static final String LANGUAGE = "be";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoCoderServiceImpl.class);

    @Autowired
    private JOpenCageGeocoder jOpenCageGeocoder;

    @Override
    public Location parse(Float longitude, Float latitude) {

        if(longitude == null || latitude == null){
            return null;
        }

        Location location = new Location();

        location.setLongitude(longitude);
        location.setLatitude(latitude);

        try {
            JOpenCageReverseRequest request = new JOpenCageReverseRequest((double) latitude, (double) longitude);
            request.setLanguage(LANGUAGE);
            JOpenCageResponse response = jOpenCageGeocoder.reverse(request);

            if(response.getTotalResults() == 0 || response.getResults().get(0).getComponents() == null){
                return location;
            }

            JOpenCageComponents jOpenCageComponents = response.getResults().get(0).getComponents();

            location.setCity(jOpenCageComponents.getCity());
            location.setCityDistrict(jOpenCageComponents.getCityDistrict());
            location.setCountry(jOpenCageComponents.getCountry());
            location.setCountryCode(jOpenCageComponents.getCountryCode());
            location.setCounty(jOpenCageComponents.getCounty());
            location.setHouseNumber(jOpenCageComponents.getHouseNumber());
            location.setPostcode(jOpenCageComponents.getPostcode());
            location.setRoad(jOpenCageComponents.getRoad());
            location.setState(jOpenCageComponents.getState());

        } catch (Exception e){
            LOGGER.warn("Error while get address by location", e);
        }

        return location;
    }
}
