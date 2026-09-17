package com.kanban.project.controller;

import com.kanban.project.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * 健康检查接口，用于前后端联通性自检与部署探活。
 */
@Tag(name = "健康检查", description = "用于联通性自检与部署探活")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final String applicationName;

    public HealthController(@Value("${spring.application.name:kanban-project}") String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * {@code GET /api/health} — 返回服务状态。
     *
     * @return 固定 {@code status=UP}、应用名与服务器当前时间（UTC）
     */
    @Operation(summary = "服务状态", description = "返回固定 status=UP、应用名与服务器当前时间（UTC），不需要任何参数。")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "服务正常",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HealthResponse.class))))
    @GetMapping
    public HealthResponse health() {
        return new HealthResponse("UP", applicationName, Instant.now());
    }
}
