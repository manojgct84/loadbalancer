package com.simple.loadbalancer.service;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements LoadBalancingStrategy {
    private final Random random = new Random();

    @Override
    public String selectServer(List<String> healthyServers) {
        System.out.println("RandomStrategy");
        if (healthyServers.isEmpty()) {
            return null;
        }
        int index = random.nextInt(healthyServers.size());
        return healthyServers.get(index);
    }
}

