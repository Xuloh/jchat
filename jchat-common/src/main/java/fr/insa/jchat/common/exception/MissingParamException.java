package fr.insa.jchat.common.exception;

public class MissingParamException extends InvalidRequestException {
    private String paramName;

    public MissingParamException(String paramName) {
        this("Missing param : " + paramName, paramName);
    }

    public MissingParamException(String message, String paramName) {
        super(message);
        this.paramName = paramName;
    }

    public MissingParamException(String message, Throwable cause, String paramName) {
        super(message, cause);
        this.paramName = paramName;
    }

    @Override
    public String getErrorName() {
        return "MISSING_PARAM";
    }
}
