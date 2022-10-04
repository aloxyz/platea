package platea.exceptions;

public class DockerException extends Exception {
    public DockerException() {
        super("Docker Engine exception!");
    }

    public DockerException(String message) {
        super(message);
    }
}
