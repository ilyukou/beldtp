package org.telegram.bot.beldtp.config;

import com.byteowls.jopencage.JOpenCageGeocoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.bot.beldtp.service.impl.GeoCoderServiceImpl;
import org.telegram.bot.beldtp.service.interf.GeoCoderService;

@Configuration
public class OpenCageGeocoderConfig {

    @Value("${jOpenCageGeocoder.api.token}")
    private String token;

    @Bean
    public JOpenCageGeocoder jOpenCageGeocoder() {
        return new JOpenCageGeocoder(token);
    }

    @Bean
    public GeoCoderService locationService() {
        return new GeoCoderServiceImpl();
    }
}
