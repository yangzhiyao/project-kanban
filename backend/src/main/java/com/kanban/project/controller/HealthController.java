package com.kanban.project.controller;

import com.kanban.project.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final String applicationName;

    public HealthController(@Value("${spring.application.name:kanban-project}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping
    public HealthResponse health() {
        return new HealthResponse("UP", applicationName, Instant.now());
    }
}
