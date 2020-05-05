package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.Location;

public interface LocationRepository {

    Location save(Location location);

    void delete(Location location);
}
