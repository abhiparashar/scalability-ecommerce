package com.scalability_ecommerce.scalability_ecommerce.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetrics {

    private LocalDateTime timestamp;
    private String operation;
    private String service;
    private long duration; // milliseconds
    private boolean success;
    private String errorMessage;
    private int threadCount;
    private long memoryUsed;
    private double cpuUsage;

    // Database metrics
    private int activeConnections;
    private int idleConnections;
    private long queryTime;

    // Cache metrics
    private long cacheHits;
    private long cacheMisses;
    private double cacheHitRatio;

    // Request metrics
    private int requestsPerSecond;
    private long responseTime;
    private int errorCount;

    public static PerformanceMetrics createDatabaseMetric(String operation, long duration, boolean success) {
        return PerformanceMetrics.builder()
                .timestamp(LocalDateTime.now())
                .operation(operation)
                .service("database")
                .duration(duration)
                .success(success)
                .build();
    }

    public static PerformanceMetrics createCacheMetric(String operation, long duration, boolean hit) {
        return PerformanceMetrics.builder()
                .timestamp(LocalDateTime.now())
                .operation(operation)
                .service("cache")
                .duration(duration)
                .success(true)
                .cacheHits(hit ? 1 : 0)
                .cacheMisses(hit ? 0 : 1)
                .build();
    }
}
