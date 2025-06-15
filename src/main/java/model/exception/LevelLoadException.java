package model.exception;

// Puedes crear una nueva carpeta 'exception' dentro de 'model'

public class LevelLoadException extends Exception {
    public LevelLoadException(String message) {
        super(message);
    }

    public LevelLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}