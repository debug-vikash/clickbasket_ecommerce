package site.clickbasketecom.ClickBasket.exception;

/**
 * Exception thrown when email already exists during registration.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
