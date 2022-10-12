package platea.exceptions.docker;

public class DeleteImageException extends Exception {
    public DeleteImageException() {
        super("Could not delete image");
    }

    public DeleteImageException(String message) {
        super(message);
    }
}
