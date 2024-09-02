package com.ariba.sap.loadbalancer.status;

import java.util.Map;

public class ServerLoadChecker
{
    private final Map<String, Integer> serverLoadMap; // Keeps track of the current load per server

    public ServerLoadChecker(Map<String, Integer> serverLoadMap) {
        this.serverLoadMap = serverLoadMap;
    }

    public int getCurrentLoad(String server) {
        return serverLoadMap.getOrDefault(server, 0); // Default to 0 if server not found
    }

    public void incrementLoad(String server) {
        serverLoadMap.put(server, serverLoadMap.getOrDefault(server, 0) + 1);
    }

    public void decrementLoad(String server) {
        serverLoadMap.put(server, serverLoadMap.getOrDefault(server, 0) - 1);
    }
}

