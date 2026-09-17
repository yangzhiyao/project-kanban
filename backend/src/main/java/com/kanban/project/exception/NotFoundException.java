package com.kanban.project.exception;

/**
 * 目标资源不存在或已被软删除，对应 HTTP 404。
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
