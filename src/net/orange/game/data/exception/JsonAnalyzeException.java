package net.orange.game.data.exception;

public class JsonAnalyzeException extends DataException {

    public JsonAnalyzeException(String message) {
        super(message);
    }

    public JsonAnalyzeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonAnalyzeException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
