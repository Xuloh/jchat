package fr.insa.jchat.common.exception;

public class InvalidBodySizeException extends InvalidRequestException {
    private int expected;

    private int actual;

    public InvalidBodySizeException(int expected, int actual) {
        this("Expected body size of " + expected + " but got body of size " + actual, expected, actual);
    }

    public InvalidBodySizeException(String message, int expected, int actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    public InvalidBodySizeException(String message, Throwable cause, int expected, int actual) {
        super(message, cause);
        this.expected = expected;
        this.actual = actual;
    }

    public int getExpected() {
        return this.expected;
    }

    public int getActual() {
        return this.actual;
    }

    @Override
    public String getErrorName() {
        return "INVALID_BODY_SIZE";
    }
}
