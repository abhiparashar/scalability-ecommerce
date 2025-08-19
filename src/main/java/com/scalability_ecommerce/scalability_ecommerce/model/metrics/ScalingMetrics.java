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
public class ScalingMetrics {

    private LocalDateTime timestamp;
    private double cpuUtilization;
    private double memoryUtilization;
    private int activeConnections;
    private int requestQueueLength;
    private double averageResponseTime;
    private int errorRate;
    private int throughput; // requests per second

    // Auto-scaling thresholds
    private static final double CPU_SCALE_UP_THRESHOLD = 70.0;
    private static final double CPU_SCALE_DOWN_THRESHOLD = 30.0;
    private static final double MEMORY_SCALE_UP_THRESHOLD = 80.0;
    private static final double MEMORY_SCALE_DOWN_THRESHOLD = 40.0;
    private static final int RESPONSE_TIME_THRESHOLD = 500; // milliseconds

    public boolean shouldScaleUp() {
        return cpuUtilization > CPU_SCALE_UP_THRESHOLD ||
                memoryUtilization > MEMORY_SCALE_UP_THRESHOLD ||
                averageResponseTime > RESPONSE_TIME_THRESHOLD ||
                errorRate > 5; // 5% error rate
    }

    public boolean shouldScaleDown() {
        return cpuUtilization < CPU_SCALE_DOWN_THRESHOLD &&
                memoryUtilization < MEMORY_SCALE_DOWN_THRESHOLD &&
                averageResponseTime < 200 &&
                errorRate < 1;
    }

    public int getScalingFactor() {
        if (cpuUtilization > 90 || memoryUtilization > 95) {
            return 3; // Aggressive scaling
        } else if (shouldScaleUp()) {
            return 2; // Moderate scaling
        } else if (shouldScaleDown()) {
            return -1; // Scale down by one instance
        }
        return 0; // No scaling needed
    }
}
