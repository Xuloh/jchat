package fr.insa.jchat.common.exception;

public class InvalidLoginException extends InvalidRequestException {
    public InvalidLoginException() {
    }

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorName() {
        return "INVALID_LOGIN";
    }
}
