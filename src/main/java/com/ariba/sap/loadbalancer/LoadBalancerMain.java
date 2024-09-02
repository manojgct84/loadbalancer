package com.ariba.sap.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Load balancer application
 */
@SpringBootApplication
@EnableScheduling
public class LoadBalancerMain
{
    public static void main( String[] args )
    {
        SpringApplication.run(LoadBalancerMain.class, args);

    }
}
