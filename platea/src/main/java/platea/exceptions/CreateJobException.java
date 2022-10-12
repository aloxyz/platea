package platea.exceptions;

public class CreateJobException extends Exception {
    public CreateJobException() {
        super("Could not create job");
    }

    public CreateJobException(String message) {
        super(message);
    }
}
