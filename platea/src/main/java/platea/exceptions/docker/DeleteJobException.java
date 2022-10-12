package platea.exceptions.docker;

public class DeleteJobException extends Exception {
    public DeleteJobException() {
        super("Could not delete job");
    }

    public DeleteJobException(String message) {
        super(message);
    }
}
