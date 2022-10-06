package platea.exceptions;

public class DeleteContainerException extends Exception {
    public DeleteContainerException() {
        super("Could not delete container");
    }

    public DeleteContainerException(String message) {
        super(message);
    }
}
