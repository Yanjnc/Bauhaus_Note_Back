package com.note.back.exception;

import com.note.back.Response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Response handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.error("业务异常: {}", e.getMessage(), e);
        Response.ErrorInfo errorInfo = new Response.ErrorInfo(e.getErrorCode(), e.getDetails(), "BUSINESS_ERROR");
        return new Response(e.getStatusCode(), e.getMessage(), null, errorInfo, request.getRequestURI());
    }

    // 处理参数验证异常
    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.error("参数验证异常: {}", e.getMessage(), e);
        Response.ErrorInfo errorInfo = new Response.ErrorInfo("BAD_REQUEST", e.getMessage(), "VALIDATION_ERROR");
        return new Response(400, e.getMessage(), null, errorInfo, request.getRequestURI());
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public Response handleGlobalException(Exception e, HttpServletRequest request) {
        logger.error("系统异常: {}", e.getMessage(), e);
        Response.ErrorInfo errorInfo = new Response.ErrorInfo("INTERNAL_SERVER_ERROR", e.getMessage(), "SYSTEM_ERROR");
        return new Response(500, "服务器内部错误，请稍后重试", null, errorInfo, request.getRequestURI());
    }
}