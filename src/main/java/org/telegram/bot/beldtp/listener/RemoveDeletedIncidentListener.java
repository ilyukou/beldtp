package org.telegram.bot.beldtp.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;

@Component
public class RemoveDeletedIncidentListener {

    private static final long REPEAT_TIME = 60 * 60 * 1000; // hour

    @Autowired
    private IncidentService incidentService;

    @Scheduled(fixedRate = REPEAT_TIME)
    public void publishIncident() {

        for (Incident incident : incidentService.get(IncidentType.DELETE)){
            incidentService.delete(incident);
        }
    }
}
