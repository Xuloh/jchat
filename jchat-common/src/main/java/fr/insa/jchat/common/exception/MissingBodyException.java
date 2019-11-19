package fr.insa.jchat.common.exception;

public class MissingBodyException extends InvalidRequestException {
    public MissingBodyException() {
    }

    public MissingBodyException(String message) {
        super(message);
    }

    public MissingBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorName() {
        return "MISSING_BODY";
    }
}
