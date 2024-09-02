package com.simple.loadbalancer.service;

import com.simple.loadbalancer.status.ServerLoadChecker;
import com.simple.loadbalancer.status.ServerStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


/**
 * The {@code LoadBalancerService} class is responsible for handling the load balancing of incoming
 * HTTP requests across multiple backend servers. It forwards requests to the appropriate server
 * based on the selected healthy load balancing strategy and manages error handling, server health
 * checks,and load distribution.
 *
 * <p>This service utilizes a specified {@link LoadBalancingStrategy} to determine the target server
 * for each request and a {@link RestTemplate} to forward the requests to the selected server.
 *
 * <p>The class supports handling both standard HTTP json requests and multipart file uploads. It is
 * designed to be stateless to ensure scalability and fault tolerance in a distributed environment.
 *
 * <p>Usage example:
 * <pre>
 *     LoadBalancerService loadBalancerService = new LoadBalancerService(loadBalancingStrategy,
 *     restTemplate, serverLoadChecker);
 *     ResponseEntity<?> response = loadBalancerService.forwardRequest(HttpMethod.POST, headers,
 *     body, "<backend api>", "file");
 * </pre>
 *
 * @author Manojkumar M
 * @version 1.0
 * @since 2024-08-29
 */
@Service
public class LoadBalancerService
{
    /* List of all healthy server */
    private final List<String> healthyServers = new CopyOnWriteArrayList<>();
    /* Get the current server index */
    /*This is to test the health of the service */
    private final RestTemplate restTemplate = new RestTemplate();
    // Strategy Interface to get different routing algo
    private final LoadBalancingStrategy loadBalancingStrategy;
    /* This is filter to know the which service is having high failure */
    private final Map<String, ServerStatus> serverStatusMap = new ConcurrentHashMap<>();
    // Max number of failures before blacklisting
    private final int failureThreshold = 3;
    // Duration to keep a server blacklisted
    private final int blacklistDurationMinutes = 5;
    /* List of all server  that is configured */
    private List<String> allServers = new ArrayList<>();

    private ServerLoadChecker serverLoadChecker;

    /**
     * Constructs a new {@code LoadBalancerService} with the specified load balancing strategy,
     * RestTemplate, and server load checker.
     *
     * @param loadBalancingStrategy the load balancing strategy to use for selecting a target server
     * @param restTemplate          the RestTemplate instance for forwarding HTTP requests
     * @param serverLoadChecker     the server load checker for monitoring and managing server loads
     */
    public LoadBalancerService (LoadBalancingStrategy loadBalancingStrategy, ServerLoadChecker serverLoadChecker)
    {
        this.loadBalancingStrategy = loadBalancingStrategy;
        this.serverLoadChecker = serverLoadChecker;
        initializeServers();
    }

    public void initializeServers ()
    {
        // Initialize with a list of servers (this can also come from loadbalancer properties)
        loadServerConfig();
    }

    /**
     * Loads the server configuration from a properties file and initializes the list of all servers
     * and healthy servers.
     *
     * <p>This method reads the configuration properties from the file located at
     * {@code config/loadbalancer.properties}. It loads the server information specified under
     * the {@code servers} property, which is a comma-separated list of server URLs.
     *
     * <p>The servers listed in the properties file are added to the {@code allServers} list and
     * are initially assumed to be healthy. Therefore, all servers are also added to the {@code
     * healthyServers} list at the start.
     *
     * <p>If there is an error while loading the properties file (e.g., file not found or I/O error)
     * , an error message is printed to the standard error stream.
     *
     * <p>Usage example:
     * <pre>
     *     private void loadServerConfig() {
     *         // Implementation to load server configuration
     *     }
     * </pre>
     *
     * @see ClassPathResource
     * @see Properties
     */
    private void loadServerConfig ()
    {
        Properties properties = new Properties();
        try (InputStream input = new ClassPathResource("config/loadbalancer.properties").getInputStream()) {
            properties.load(input);
            String servers = properties.getProperty("servers");
            if (servers != null) {
                allServers = new ArrayList<>(Arrays.asList(servers.split(",")));
                healthyServers.addAll(allServers); // Initially, assume all servers are healthy
            }
        }
        catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
        }
    }


    /**
     * Periodically performs health checks on all registered backend servers to determine their
     * availability and status.
     *
     * <p>This method is scheduled to run at a fixed interval, as specified by the configuration
     * property {@code loadbalancer.healthcheck.interval}. It iterates through all servers in the
     * system and checks their health status. If a server is found to be healthy but is not currently
     * marked as healthy, it is added to the list of healthy servers. Conversely, if a server is
     * found to be unhealthy, it is removed from the list of healthy servers.
     *
     * <p>Logging messages are printed to the console to indicate changes in server status
     * (either a server coming back online or going down).
     *
     * <p>This method ensures that the load balancer has an up-to-date list of healthy servers
     * to forward incoming requests to, thereby enhancing fault tolerance and system resilience.
     *
     * <p>Usage example:
     * <pre>
     *     &#64;Scheduled(fixedDelayString = "${loadbalancer.healthcheck.interval}")
     *     public void performHealthChecks() {
     *         // Implementation of health checks
     *     }
     * </pre>
     *
     * @see #checkServerHealth(String)
     */
    @Scheduled(fixedDelayString = "${loadbalancer.healthcheck.interval}")
    public void performHealthChecks ()
    {
        for (String server : allServers) {
            if (checkServerHealth(server)) {
                if (!healthyServers.contains(server)) {
                    healthyServers.add(server);
                    System.out.println("Server " + server + " is back online.");
                }
            }
            else {
                healthyServers.remove(server);
                System.out.println("Server " + server + " is down or unhealthy.");
            }
        }
    }

    // Check server health by hitting the /health endpoint
    private boolean checkServerHealth (String server)
    {
        try {
            String healthUrl = server + "/health";
            restTemplate.getForEntity(healthUrl, String.class);
            return true; // Server is healthy if no exception is thrown
        }
        catch (Exception e) {
            return false; // Server is unhealthy if an exception occurs
        }
    }


    /**
     * Forwards an HTTP request to the selected backend server based on the load balancing strategy.
     *
     * @param method    the HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param headers   the HTTP headers to be included in the request
     * @param body      the request body or {@link MultipartFile} in case of a file upload
     * @param targetURL the target URL path of the backend service API
     * @param type      the type of request, either "file" for file uploads or {@code null} for
     *                  standard requests
     * @return a {@link ResponseEntity} containing the response from the backend server or an
     * error message if forwarding fails
     */
    public ResponseEntity<?> forwardRequest (HttpMethod method,
                                             HttpHeaders headers,
                                             Object body,
                                             String targetURL,
                                             String type) throws IOException
    {
        String server = loadBalancingStrategy.selectServer(healthyServers);
        if (server == null) {
            System.out.println("Request failed. No available servers.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("No available servers");
        }

        String fullUrl = server + "/" + targetURL; // Construct full URL
        System.out.println("Forwarding request to: " + fullUrl);
        HttpEntity<?> requestEntity;
        if ("file".equals(type)) {
            // Ensure 'body' is of type MultipartFile
            if (body instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) body;
                try {
                    // Use ByteArrayResource to handle file contents properly
                    ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes())
                    {
                        @Override
                        public String getFilename ()
                        {
                            return file.getOriginalFilename();
                        }
                    };

                    MultiValueMap<String, Object> mfile = new LinkedMultiValueMap<>();
                    mfile.add("file", fileAsResource);

                    // Create HttpEntity for multipart request
                    requestEntity = new HttpEntity<>(mfile, headers);
                }
                catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
                }
            }
            else {
                System.out.println("Invalid body type for file upload.");
                return ResponseEntity.badRequest().body("Invalid body type for file upload.");
            }
        }
        else {
            // Handle regular POST request
            requestEntity = new HttpEntity<>(body, headers);
        }
        // Forward the request using RestTemplate
        try {
            serverLoadChecker.incrementLoad(server);
            ResponseEntity<?> responseEntity = restTemplate.exchange(fullUrl, method,
                requestEntity,
                String.class);
            resetServerFailureCount(server);
            return responseEntity;
        }
        catch (Exception e) {
            System.out.println("Failed to forward request: " + e.getMessage());
            incrementServerFailureCount(server);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request");
        }
        finally {
            // Decrement load after request completes, regardless of success or failure
            serverLoadChecker.decrementLoad(server);
        }
    }

    private void resetServerFailureCount (String server)
    {
        ServerStatus status = serverStatusMap.get(server);
        if (status != null) {
            status.resetFailureCount();
        }
    }

    /**
     * Adds filtering logic to blacklist servers that have a high connection failure rate,
     * to ensure reliable load balancing.
     *
     * @param server the server name of the blacklisted.
     */
    private void incrementServerFailureCount (String server)
    {
        ServerStatus status = serverStatusMap.get(server);
        if (status != null) {
            status.incrementFailureCount();
            if (status.getFailureCount() >= failureThreshold) {
                status.setBlacklisted(true);
                System.out.println("Server " + server + " has been blacklisted due to repeated failures.");
            }
        }
    }

    /**
     * Add new server backend to the existing servers.
     *
     * @param serverUrl the host name url of the new servers
     */
    public void addServer (String serverUrl)
    {
        allServers.add(serverUrl);
        healthyServers.add(serverUrl);
        System.out.println("Server " + serverUrl + " added successfully.");
    }
}

