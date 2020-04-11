package org.telegram.bot.beldtp.listener.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.BeldtpBot;
import org.telegram.bot.beldtp.model.Incident;
import org.telegram.bot.beldtp.model.IncidentType;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;

import java.util.List;

@Component
public class ChannelPublishListener {

    @Value("${bot.channel.username}")
    private String url;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private BeldtpBot beldtpBot;

    @Scheduled(fixedRate = 10000)
    public void publishIncident() {

        List<Incident> incidents = incidentService.get(IncidentType.VERIFY);

        if (incidents != null && incidents.size() > 0) {
            incidents.forEach(this::publish);
        }
    }

    private void publish(Incident incident) {

        beldtpBot.executeTelegramResponse(
                new TelegramResponse(incidentService.getSendMediaGroup(incident).setChatId(url))
        );

        incident.setType(IncidentType.PUBLISH);
        incident = incidentService.save(incident);
    }
}
