package platea.exceptions;

public class DatabaseDeleteException extends Exception {
    public DatabaseDeleteException() {
        super("Could not delete record in database");
    }

    public DatabaseDeleteException(String message) {
        super(message);
    }
}
