package platea.exceptions;

public class DockerCreateImageException extends Exception {
    public DockerCreateImageException() {
        super("Could not create image");
    }

    public DockerCreateImageException(String message) {
        super(message);
    }
}
