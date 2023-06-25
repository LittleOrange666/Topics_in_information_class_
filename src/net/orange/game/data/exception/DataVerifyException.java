package net.orange.game.data.exception;

public class DataVerifyException extends DataException {

    public DataVerifyException(String message) {
        super(message);
    }

    public DataVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataVerifyException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
