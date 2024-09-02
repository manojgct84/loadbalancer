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


### Summary

This README covers essential steps for installing, configuring, and using the Load Balancer. It details the key features, describes how to adjust server settings, run health checks, and outlines the load balancing strategies. Additionally, it provides instructions on how to run the application, forward requests, and handle errors, making it easy for users to get started with the service.


