package platea.exceptions;

public class DatabaseInsertContainerException extends Exception {
    public DatabaseInsertContainerException() {
        super("Could not insert container in database");
    }

    public DatabaseInsertContainerException(String message) {
        super(message);
    }
}
