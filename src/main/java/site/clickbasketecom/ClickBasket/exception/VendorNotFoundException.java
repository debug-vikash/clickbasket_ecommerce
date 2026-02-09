package site.clickbasketecom.ClickBasket.exception;

/**
 * Exception thrown when a vendor is not found.
 */
public class VendorNotFoundException extends RuntimeException {

    public VendorNotFoundException(String message) {
        super(message);
    }

    public VendorNotFoundException(Long vendorId) {
        super("Vendor not found with ID: " + vendorId);
    }
}
