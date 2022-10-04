package platea.exceptions;

public class DatabaseInsertInstanceException extends Exception {
    public DatabaseInsertInstanceException() {
        super("Could not insert instance in database");
    }

    public DatabaseInsertInstanceException(String message) {
        super(message);
    }
}
