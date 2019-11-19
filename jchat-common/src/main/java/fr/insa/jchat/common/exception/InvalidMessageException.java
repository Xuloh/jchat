package fr.insa.jchat.common.exception;

public class InvalidMessageException extends InvalidRequestException {
    private String message;

    public InvalidMessageException(String message) {
        this("Invalid message : " + message, message);
    }

    public InvalidMessageException(String message, String message1) {
        super(message);
        this.message = message1;
    }

    public InvalidMessageException(String message, Throwable cause, String message1) {
        super(message, cause);
        this.message = message1;
    }

    @Override
    public String getErrorName() {
        return "INVALID_MESSAGE";
    }
}
