package platea.exceptions;

public class CreateJobExistsException extends Exception {
    public CreateJobExistsException() {
        super("Job already exists");
    }

    public CreateJobExistsException(String message) {
        super(message);
    }
}
