package org.telegram.bot.beldtp.handler.subclasses;

import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.UserRole;

@HandlerInfo(type = "api", accessRight = UserRole.USER)
public class ApiHandler extends Handler {
}
