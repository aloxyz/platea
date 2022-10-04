package platea.exceptions;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException() {
        super("Could not connect to database");
    }

    public DatabaseConnectionException(String message) {
        super(message);
    }
}
