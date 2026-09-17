package com.kanban.project.exception;

/**
 * 业务参数不合法（如结束日期早于开始日期），对应 HTTP 400。
 *
 * <p>字段级约束由 Jakarta Bean Validation 负责，本异常用于跨字段校验。
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
