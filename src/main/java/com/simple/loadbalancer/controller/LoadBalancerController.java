package com.simple.loadbalancer.controller;

import com.simple.loadbalancer.service.LoadBalancerService;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/forwardRequest")
public class LoadBalancerController
{
    @Autowired
    private LoadBalancerService loadBalancerService;

    @GetMapping("/{path}")
    public ResponseEntity<?> handleGetRequest (
        HttpServletRequest request,
        @RequestBody(required = false) String body,
        @PathVariable("path") String targetURL) throws IOException
    {
        // Extract HTTP method
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        // Extract relative path
        // Extract headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            headers.put(headerName, headerValues);
        }
        return loadBalancerService.forwardRequest(method, headers, body, targetURL, "null");
    }

    @PostMapping("/{path}")
    public ResponseEntity<?> handlePostRequest (
        HttpServletRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file,
        @RequestBody(required = false) String body,
        @PathVariable("path") String targetURL) throws IOException
    {
        // Extract HTTP method
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        // Extract headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            headers.put(headerName, headerValues);
        }
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains("multipart/form-data")) {
            // Handle file upload request
            return loadBalancerService.forwardRequest(method, headers, file, targetURL,
                "file");
        }
        else {
            // Handle regular POST request
            return loadBalancerService.forwardRequest(method, headers, body, targetURL, "null");
        }
    }

    @PostMapping("/addServer")
    public String addServer (@RequestParam String serverUrl)
    {
        loadBalancerService.addServer(serverUrl);
        return "Server " + serverUrl + " added successfully.";
    }
}

