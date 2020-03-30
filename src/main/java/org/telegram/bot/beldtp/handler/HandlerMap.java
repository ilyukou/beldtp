package org.telegram.bot.beldtp.handler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public final class HandlerMap {
    private static Map<String, Handler> logicComponentMap = new HashMap<>();

    public HandlerMap() {
    }

    public Map<String, Handler> getLogicComponentMap() {
        return logicComponentMap;
    }

    public void put(Handler logicComponent) {
        logicComponentMap.put(logicComponent.getType(), logicComponent);
    }

    public Handler get(String key) {
        return logicComponentMap.get(key);
    }

    public boolean isExist(String key){
        return logicComponentMap.get(key) != null;
    }
}
