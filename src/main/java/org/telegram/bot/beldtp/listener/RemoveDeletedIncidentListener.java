package org.telegram.bot.beldtp.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.LocationService;
import org.telegram.bot.beldtp.service.interf.model.UserService;

@Component
public class RemoveDeletedIncidentListener {

    private static final long REPEAT_TIME = 60 * 60 * 1000; // hour

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Scheduled(fixedRate = REPEAT_TIME)
    public void publishIncident() {

        for (Incident incident : incidentService.get(IncidentType.DELETE)){
            if(incident.getLocation() != null){
                locationService.delete(incident.getLocation());
            }
            User user = incident.getUser();

            user.remove(incident);
            user = userService.save(user);

            incidentService.delete(incident);
        }
    }
}
