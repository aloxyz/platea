package platea.exceptions;

public class DatabaseInsertException extends Exception {
    public DatabaseInsertException() {
        super("Could not insert instance in database");
    }

    public DatabaseInsertException(String message) {
        super(message);
    }
}
