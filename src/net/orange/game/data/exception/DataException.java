package net.orange.game.data.exception;

public class DataException extends IllegalArgumentException {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
