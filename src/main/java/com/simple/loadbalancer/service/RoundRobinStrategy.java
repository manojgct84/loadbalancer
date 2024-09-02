package com.simple.loadbalancer.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public String selectServer(List<String> healthyServers) {
        System.out.println("RoundRobinStrategy");
        if (healthyServers.isEmpty()) {
            return null;
        }
        int index = currentIndex.getAndUpdate(i -> (i + 1) % healthyServers.size());
        return healthyServers.get(index);
    }
}

