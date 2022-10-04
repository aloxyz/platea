package platea.exceptions;

public class DatabaseGetException extends Exception {
    public DatabaseGetException() {
        super("Could not query container in database");
    }

    public DatabaseGetException(String message) {
        super(message);
    }
}
