package platea.exceptions.docker;

public class CreateImageException extends Exception {
    public CreateImageException() {
        super("Could not create image");
    }

    public CreateImageException(String message) {
        super(message);
    }
}
