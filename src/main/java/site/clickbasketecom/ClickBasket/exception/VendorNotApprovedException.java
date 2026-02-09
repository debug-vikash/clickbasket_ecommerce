package site.clickbasketecom.ClickBasket.exception;

/**
 * Exception thrown when vendor is not approved to perform an action.
 */
public class VendorNotApprovedException extends RuntimeException {

    public VendorNotApprovedException(String message) {
        super(message);
    }

    public VendorNotApprovedException() {
        super("Vendor is not approved. Only approved vendors can perform this action.");
    }
}
