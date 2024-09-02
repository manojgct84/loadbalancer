# Load Balancer Service

## Overview

The **Load Balancer Service** is a Spring Boot-based application designed to distribute incoming HTTP requests across multiple backend servers using various load balancing strategies. It manages server health checks, distributes load evenly, and can handle both standard and multipart file upload requests.

### Key Features

- **Dynamic Load Balancing**: Routes requests based on configurable strategies (e.g., Round Robin, Choice-of-2).
- **Health Checks**: Periodically checks server health and adjusts the list of healthy servers.
- **Error Handling**: Handles cases when servers are unavailable, providing fallback mechanisms.
- **Supports Multipart Uploads**: Capable of handling multipart/form-data requests.
- **Blacklist Mechanism**: Blacklists servers with high failure rates to improve reliability.

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Load Balancing Strategies](#load-balancing-strategies)
- [Endpoints](#endpoints)
- [Health Checks](#health-checks)
- [Contributing](#contributing)
- [License](#license)

## Installation

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/your-repository/load-balancer.git
    cd load-balancer
    ```

2. **Build the Project**:

   Use Maven to build the project:

    ```bash
    mvn clean install
    ```

## Configuration

### 1. `loadbalancer.properties`

The application configuration is defined in a properties file located at `src/main/resources/config/loadbalancer.properties`. This file contains the list of backend servers and other relevant settings.

Example:

```properties
# Comma-separated list of server URLs
servers=http://localhost:8081,http://localhost:8082,http://localhost:8083

# Health check interval (in milliseconds)
loadbalancer.healthcheck.interval=5000
```

3. **Running the Application**
 ```bash
mvn spring-boot:run
```

4. **Usage**
Forwarding Requests
Use Postman or any HTTP client to send requests to the Load Balancer.
The Load Balancer will forward requests to one of the healthy backend servers based on the configured load balancing strategy.
Example API Calls:
Forward Standard Request:

http
POST http://localhost:8092/forwardRequest/getValue
Forward Multipart File Request:

http
POST http://localhost:8092/forwardRequest/uploadFile
Include headers and body as needed. The Load Balancer will automatically determine how to handle the request based on content type.

5. **Load Balancing Strategies**
The system currently supports multiple load balancing strategies:

Round Robin: Distributes requests sequentially across all servers.
Choice-of-2: Selects the least loaded server from two randomly chosen servers.
Custom Strategies: Implement the LoadBalancingStrategy interface to define your own strategy.
6. **Endpoints**
 ```/forwardRequest/{path}``` : Forwards requests to the appropriate backend server based on 
   the 
   configured strategy.
```/health```: (Optional) Endpoint to check the health status of the Load Balancer itself.
Health Checks
The Load Balancer periodically checks the health of all configured servers using the ```performHealthChecks()``` method. Servers that fail the health check are removed from the list of healthy servers, and those that pass are added back if previously removed.

7. **Configuration of Health Checks**:
The interval for health checks is set via the loadbalancer.healthcheck.interval property.
Error Handling
If no servers are available, the Load Balancer responds with a 503 Service Unavailable status.
Errors during request forwarding are logged and responded to with appropriate HTTP status codes.

### Summary

This README covers essential steps for installing, configuring, and using the Load Balancer. It details the key features, describes how to adjust server settings, run health checks, and outlines the load balancing strategies. Additionally, it provides instructions on how to run the application, forward requests, and handle errors, making it easy for users to get started with the service.


