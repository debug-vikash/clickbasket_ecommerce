package site.clickbasketecom.ClickBasket.exception;

/**
 * Exception thrown when a required role is not found.
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }
}
