package platea.exceptions.database;

public class DeleteException extends Exception {
    public DeleteException() {
        super("Could not delete record in database");
    }

    public DeleteException(String message) {
        super(message);
    }
}
