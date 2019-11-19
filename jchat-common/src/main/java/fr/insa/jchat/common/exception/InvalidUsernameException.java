package fr.insa.jchat.common.exception;

public class InvalidUsernameException extends InvalidRequestException {
    private String username;

    public InvalidUsernameException(String username) {
        this("Invalid username : " + username, username);
    }

    public InvalidUsernameException(String message, String username) {
        super(message);
        this.username = username;
    }

    public InvalidUsernameException(String message, Throwable cause, String username) {
        super(message, cause);
        this.username = username;
    }

    @Override
    public String getErrorName() {
        return "INVALID_USERNAME";
    }
}
