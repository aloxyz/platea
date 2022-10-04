package platea;

public class DatabaseException extends Exception {
    public DatabaseException() {
        super("There was a problem with the database");
    }
    public DatabaseException(String errorMessage) {
        super(errorMessage);
    }
}
