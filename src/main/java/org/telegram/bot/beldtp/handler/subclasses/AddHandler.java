package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.subclasses.add.*;
import org.telegram.bot.beldtp.model.UserRole;

import java.util.Arrays;
import java.util.List;

@HandlerInfo(type = "add", accessRight = UserRole.USER)
public class AddHandler extends Handler {

    @Autowired
    private AddMediaHandler addMediaHandler;

    @Autowired
    private AddTextHandler addTextHandler;

    @Autowired
    private AddLocationHandler addLocationHandler;

    @Autowired
    private AddTimeHandler addTimeHandler;

    @Autowired
    private ConfirmAddHandler confirmAddHandler;

    @Autowired
    private BackAndRejectIncidentHandler backAndRejectIncidentHandler;

    @Override
    public List<Handler> getChild() {
        return Arrays.asList(addMediaHandler, addTextHandler, addLocationHandler,
                addTimeHandler, confirmAddHandler, backAndRejectIncidentHandler);
    }
}
