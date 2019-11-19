package fr.insa.jchat.common.exception;

public class InvalidRequestException extends Exception {
    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorName() {
        return "INVALID_REQUEST";
    }
}
