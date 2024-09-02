package com.ariba.sap.loadbalancer.status;

import java.time.LocalDateTime;

public class ServerStatus
{
    private final String serverUrl;
    private int failureCount;
    private boolean blacklisted;
    private LocalDateTime lastFailureTime;

    public ServerStatus (String serverUrl)
    {
        this.serverUrl = serverUrl;
        this.failureCount = 0;
        this.blacklisted = false;
        this.lastFailureTime = null;
    }

    public String getServerUrl ()
    {
        return serverUrl;
    }

    public int getFailureCount ()
    {
        return failureCount;
    }

    public void incrementFailureCount ()
    {
        this.failureCount++;
        this.lastFailureTime = LocalDateTime.now();
    }

    public boolean isBlacklisted ()
    {
        return blacklisted;
    }

    public void setBlacklisted (boolean blacklisted)
    {
        this.blacklisted = blacklisted;
    }

    public LocalDateTime getLastFailureTime ()
    {
        return lastFailureTime;
    }

    public void resetFailureCount ()
    {
        this.failureCount = 0;
        this.blacklisted = false;
        this.lastFailureTime = null;
    }
}

