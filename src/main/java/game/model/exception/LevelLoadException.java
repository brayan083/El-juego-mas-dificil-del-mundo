package game.model.exception;

public class LevelLoadException extends Exception {
    public LevelLoadException(String message) {
        super(message);
    }

    public LevelLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}