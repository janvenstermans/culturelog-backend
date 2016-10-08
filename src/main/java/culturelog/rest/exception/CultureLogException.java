package culturelog.rest.exception;

/**
 * @author Jan Venstermans
 */
public class CultureLogException extends Exception {
    public CultureLogException(String message) {
        super(message);
    }

    public CultureLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public CultureLogException(Throwable cause) {
        super(cause);
    }
}