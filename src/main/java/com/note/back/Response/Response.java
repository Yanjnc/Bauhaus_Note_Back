package com.note.back.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    private int status;
    private String message;
    private Object object;
    private ErrorInfo error;
    private String timestamp;
    private String path;

    public Response(int status, String message, Object object){
        this.status = status;
        this.message = message;
        this.object = object;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public Response(int status, String message, Object object, String path){
        this.status = status;
        this.message = message;
        this.object = object;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.path = path;
    }

    public Response(int status, String message, Object object, ErrorInfo error, String path){
        this.status = status;
        this.message = message;
        this.object = object;
        this.error = error;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 内部类：错误信息
    public static class ErrorInfo {
        private String code;
        private String details;
        private String type;

        public ErrorInfo(String code, String details, String type) {
            this.code = code;
            this.details = details;
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
