package site.clickbasketecom.ClickBasket.exception;

/**
 * Exception thrown when a category is not found.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long categoryId) {
        super("Category not found with ID: " + categoryId);
    }
}
