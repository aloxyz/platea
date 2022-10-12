package platea.exceptions.database;

public class InsertException extends Exception {
    public InsertException() {
        super("Could not insert instance in database");
    }

    public InsertException(String message) {
        super(message);
    }
}
