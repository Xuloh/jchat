package fr.insa.jchat.common.exception;

public class InvalidMethodException extends InvalidRequestException {
    private String method;

    public InvalidMethodException(String method) {
        this("Invalid method name " + method, method);
    }

    public InvalidMethodException(String message, String method) {
        super(message);
        this.method = method;
    }

    public InvalidMethodException(String message, Throwable cause, String method) {
        super(message, cause);
        this.method = method;
    }

    @Override
    public String getErrorName() {
        return "INVALID_METHOD";
    }
}
