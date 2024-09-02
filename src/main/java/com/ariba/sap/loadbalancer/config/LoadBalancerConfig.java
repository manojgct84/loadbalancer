package com.ariba.sap.loadbalancer.config;

import com.ariba.sap.loadbalancer.service.LeastConnectionsStrategy;
import com.ariba.sap.loadbalancer.service.LoadBalancingStrategy;
import com.ariba.sap.loadbalancer.service.NetflixChoiceOfTwoStrategy;
import com.ariba.sap.loadbalancer.service.RandomStrategy;
import com.ariba.sap.loadbalancer.service.RoundRobinStrategy;
import com.ariba.sap.loadbalancer.status.ServerLoadChecker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalancerConfig
{

    @Value("${loadbalancer.strategy}")
    private String strategy;

    @Bean
    public LoadBalancingStrategy loadBalancingStrategy ()
    {
        return switch (strategy.toLowerCase()) {
            case "round-robin" -> new RoundRobinStrategy();
            case "least-connections" -> new LeastConnectionsStrategy();
            case "random" -> new RandomStrategy();
            case "choiceoftwostrategy" -> new NetflixChoiceOfTwoStrategy(serverLoadChecker());
            default ->
                throw new IllegalArgumentException("Invalid load balancing strategy: " + strategy);
        };
    }

    @Bean
    public ServerLoadChecker serverLoadChecker ()
    {
        return new ServerLoadChecker(new HashMap<>());
    }
}

