package fr.insa.jchat.common.exception;

public class InvalidParamValue extends InvalidRequestException {
    private String paramName;

    public InvalidParamValue(String paramName) {
        this.paramName = paramName;
    }

    public InvalidParamValue(String message, String paramName) {
        super(message);
        this.paramName = paramName;
    }

    public InvalidParamValue(String message, Throwable cause, String paramName) {
        super(message, cause);
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    @Override
    public String getErrorName() {
        return "INVALID_PARAM_VALUE";
    }
}
