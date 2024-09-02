package com.simple.loadbalancer.service;

import com.simple.loadbalancer.status.ServerLoadChecker;

import java.util.List;

import java.util.Random;

public class NetflixChoiceOfTwoStrategy implements LoadBalancingStrategy {
    private final Random random = new Random();
    private final ServerLoadChecker serverLoadChecker; // Assume a utility to get the current load

    public NetflixChoiceOfTwoStrategy(ServerLoadChecker serverLoadChecker) {
        this.serverLoadChecker = serverLoadChecker;
    }

    @Override
    public String selectServer(List<String> servers) {
        System.out.println("NetflixChoiceOfTwoStrategy");
        if (servers == null || servers.size() == 0) {
            return null; // No servers available
        }
        if (servers.size() == 1) {
            return servers.get(0); // Only one server available
        }

        // Randomly select two different servers
        int firstIndex = random.nextInt(servers.size());
        int secondIndex;
        do {
            secondIndex = random.nextInt(servers.size());
        } while (firstIndex == secondIndex);

        String server1 = servers.get(firstIndex);
        String server2 = servers.get(secondIndex);

        // Compare their loads and select the one with the least load
        int load1 = serverLoadChecker.getCurrentLoad(server1);
        int load2 = serverLoadChecker.getCurrentLoad(server2);

        return (load1 <= load2) ? server1 : server2;
    }
}

