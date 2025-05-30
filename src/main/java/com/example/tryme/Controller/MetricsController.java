package com.example.tryme.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tryme.services.RequestCounter;

@RestController
@RequestMapping("/stats")
public class MetricsController {
    private final RequestCounter requestCounter;

    public MetricsController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping("/requests")
    public Map<String, Integer> getMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("totalRequests", requestCounter.getTotalRequests());
        metrics.put("successfulRequests", requestCounter.getSuccessfulRequests());
        metrics.put("failedRequests", requestCounter.getFailedRequests());
        return metrics;
    }
}