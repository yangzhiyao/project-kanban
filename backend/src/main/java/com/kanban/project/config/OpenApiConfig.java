package com.kanban.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档配置。
 *
 * <p>文档由 springdoc 扫描 Controller/注解自动生成，JSON 端点位于
 * {@code springdoc.api-docs.path}（见 application.yml，默认 {@code /api/openapi}），
 * 前端「接口文档」页面直接消费该 JSON。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kanbanOpenApi() {
        return new OpenAPI().info(new Info()
                .title("项目看板 API")
                .version("v1")
                .description("""
                        项目看板后端接口。统一前缀 /api，JSON（UTF-8）。
                        错误统一返回 RFC 7807 ProblemDetail：
                        {"status":400,"title":"...","detail":"...","instance":"/api/projects","errors":{"field":"message"}}，
                        其中 errors 仅在字段校验失败（400）时出现。
                        """));
    }
}
