package dev.deskriders.printrider.api.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable e) {
        super(message, e);
    }
}
