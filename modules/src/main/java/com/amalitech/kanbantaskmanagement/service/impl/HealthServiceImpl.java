package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.config.ApiVersionConfig;
import com.amalitech.kanbantaskmanagement.dto.health.HealthData;
import com.amalitech.kanbantaskmanagement.service.HealthService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HealthServiceImpl implements HealthService {

    private final HealthEndpoint healthEndpoint;
    private final ApiVersionConfig apiVersionConfig;

    public HealthServiceImpl(HealthEndpoint healthEndpoint,
                             ApiVersionConfig apiVersionConfig) {
        this.healthEndpoint = healthEndpoint;
        this.apiVersionConfig = apiVersionConfig;
    }

    @Override
    public HealthData getHealthStatus() {
        HealthComponent healthComponent = healthEndpoint.health();

        return new HealthData(
                healthComponent.getStatus().getCode(),
                apiVersionConfig.getVersion(),
                formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()),
                extractDependencies(healthComponent)
        );
    }

    private Map<String, String> extractDependencies(HealthComponent healthComponent) {
        Map<String, String> dependencies = new LinkedHashMap<>();

        if (healthComponent instanceof Health health) {
            health.getDetails().forEach((componentName, componentHealth) -> {
                if (componentHealth instanceof Health subHealth) {
                    dependencies.put(componentName, subHealth.getStatus().getCode());
                } else if (componentHealth instanceof HealthComponent hc) {
                    dependencies.put(componentName, hc.getStatus().getCode());
                }
            });
        }

        if (dependencies.isEmpty()) {
            dependencies.put("application", healthComponent.getStatus().getCode());
        }

        return dependencies;
    }

    private String formatUptime(long uptimeMillis) {
        Duration duration = Duration.ofMillis(uptimeMillis);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}