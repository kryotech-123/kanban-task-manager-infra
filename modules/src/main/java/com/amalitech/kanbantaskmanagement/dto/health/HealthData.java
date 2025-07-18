package com.amalitech.kanbantaskmanagement.dto.health;

import java.util.Map;

public record HealthData(
        String status,
        String version,
        String uptime,
        Map<String, String> dependencies
) {}
