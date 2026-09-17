package com.kanban.project.exception;

/**
 * 项目编号已被占用，对应 HTTP 409。
 */
public class DuplicateCodeException extends RuntimeException {

    public DuplicateCodeException(String message) {
        super(message);
    }
}
