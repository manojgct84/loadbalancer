package com.simple.loadbalancer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeastConnectionsStrategy implements LoadBalancingStrategy
{
    private final Map<String, Integer> serverConnections = new HashMap<>(); // Active connections count

    @Override
    public String selectServer (List<String> healthyServers)
    {
        System.out.println("LeastConnectionsStrategy");
        if (healthyServers.isEmpty()) {
            return null;
        }

        // Initialize connections count for new servers
        for (String server : healthyServers) {
            serverConnections.putIfAbsent(server, 0);
        }

        // Find the server with the least number of connections
        String selectedServer = healthyServers.get(0);
        int minConnections = serverConnections.get(selectedServer);

        for (String server : healthyServers) {
            int connections = serverConnections.get(server);
            if (connections < minConnections) {
                minConnections = connections;
                selectedServer = server;
                updateConnections(server, minConnections);
            }
        }
        return selectedServer;
    }

    // Method to update connection count after each request handling (to be called externally)
    public void updateConnections (String server, int delta)
    {
        serverConnections.put(server, serverConnections.getOrDefault(server, 0) + delta);
    }
}

