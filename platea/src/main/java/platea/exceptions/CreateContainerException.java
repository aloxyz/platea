package platea.exceptions;

public class CreateContainerException extends Exception {
    public CreateContainerException() {
        super("Could not create container");
    }

    public CreateContainerException(String message) {
        super(message);
    }
}
