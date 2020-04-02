package org.telegram.bot.beldtp.service.interf;

import org.telegram.bot.beldtp.model.Location;

public interface GeoCoderService {

    Location parse(Float longitude, Float latitude);
}
