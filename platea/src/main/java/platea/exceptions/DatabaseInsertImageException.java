package platea.exceptions;

public class DatabaseInsertImageException extends Exception {
    public DatabaseInsertImageException() {
        super("Could not insert image in database");
    }

    public DatabaseInsertImageException(String message) {
        super(message);
    }
}
