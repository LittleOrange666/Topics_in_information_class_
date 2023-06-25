package net.orange.game.data.exception;

public class DataIOException extends DataException {

    public DataIOException(String message) {
        super(message);
    }

    public DataIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIOException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
