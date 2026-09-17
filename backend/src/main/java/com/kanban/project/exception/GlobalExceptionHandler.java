package com.kanban.project.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理，把业务/框架异常统一转换成 RFC 7807 {@link ProblemDetail}。
 *
 * <p>响应体结构：{@code {"status":400,"title":"...","detail":"...","instance":"/api/projects","errors":{...}}}，
 * 其中 {@code errors} 仅在字段校验失败时出现，键为字段名、值为错误提示。
 *
 * <p>映射关系：
 * <ul>
 *   <li>{@link NotFoundException} -&gt; 404</li>
 *   <li>{@link DuplicateCodeException}、{@link DataIntegrityViolationException} -&gt; 409</li>
 *   <li>{@link InvalidRequestException}、{@link MethodArgumentNotValidException}、
 *       {@link MethodArgumentTypeMismatchException}、{@link HttpMessageNotReadableException} -&gt; 400</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "资源不存在", ex.getMessage());
    }

    @ExceptionHandler(DuplicateCodeException.class)
    public ProblemDetail handleDuplicateCode(DuplicateCodeException ex) {
        return problem(HttpStatus.CONFLICT, "项目编号重复", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "数据冲突", "数据违反唯一性或完整性约束");
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail handleInvalidRequest(InvalidRequestException ex) {
        return problem(HttpStatus.BAD_REQUEST, "请求参数不合法", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "请求参数校验失败", "共 " + errors.size() + " 项校验未通过");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "参数类型不正确",
                "参数 " + ex.getName() + " 的值不合法: " + ex.getValue());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex) {
        return problem(HttpStatus.BAD_REQUEST, "请求体解析失败", "请求体格式不正确或枚举值无效");
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
