package com.simple.loadbalancer.service;

import java.util.List;

/**
 * Interface is to provide a flexible mechanism for selecting an appropriate server from
 * a list of healthy servers based on different balancing algorithms
 * (e.g., Round-Robin, Choice-of-2, Random, etc.).
 */
public interface LoadBalancingStrategy
{
    /**
     * Selects a server from the list of healthy servers based on the implemented load
     * balancing strategy.
     *
     * @param healthyServers a list of healthy servers that are currently available for
     *                       handling requests. This list should not be null or empty.
     * @return the selected server's URL as a String. Returns null if no server could be selected.
     */
    String selectServer (List<String> healthyServers);
}
