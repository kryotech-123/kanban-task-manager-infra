package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.config.ApiVersionConfig;
import com.amalitech.kanbantaskmanagement.dto.health.HealthData;
import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;
import com.amalitech.kanbantaskmanagement.service.HealthService;

import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/status")
public class HealthController {
    private final HealthService healthService;
    private final ApiVersionConfig apiVersionConfig;

    public HealthController(HealthService healthService,
                            ApiVersionConfig apiVersionConfig) {
        this.healthService = healthService;
        this.apiVersionConfig = apiVersionConfig;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HealthData>> getHealthStatus() {
        HealthData healthData = healthService.getHealthStatus();
        String apiVersion = apiVersionConfig.getVersion();

        boolean equals = healthData.status().equals(Status.UP.getCode());
        HttpStatus httpStatus = equals
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;

        String message = String.format("API %s status: %s",
                apiVersion,
                equals
                        ? "Operational"
                        : "Degraded");

        return new ResponseEntity<>(ApiResponse.success(healthData, message), httpStatus);
    }
}
