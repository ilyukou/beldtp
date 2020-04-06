package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.queue.ReadyQueueHandler;
import org.telegram.bot.beldtp.handler.subclasses.queue.RejectQueueHandler;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.UserService;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "queue", accessRight = UserRole.MODERATOR)
public class QueueHandler extends Handler {

    @Autowired
    private ReadyQueueHandler readyQueueHandler;

    @Autowired
    private RejectQueueHandler rejectQueueHandler;

    @Autowired
    private BackHandler backHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private IncidentService incidentService;

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(readyQueueHandler, rejectQueueHandler, backHandler);
    }
}
