package fr.insa.jchat.common.exception;

public class InvalidSessionException extends InvalidRequestException {
    public InvalidSessionException() {
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorName() {
        return "INVALID_SESSION";
    }
}
