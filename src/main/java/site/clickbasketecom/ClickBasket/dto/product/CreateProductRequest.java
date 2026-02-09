package site.clickbasketecom.ClickBasket.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating a new product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "Compare at price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Invalid compare at price format")
    private BigDecimal compareAtPrice;

    @DecimalMin(value = "0.00", message = "Cost price must be at least 0.00")
    @Digits(integer = 10, fraction = 2, message = "Invalid cost price format")
    private BigDecimal costPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    private Integer lowStockThreshold;

    @Size(max = 500, message = "Main image URL must not exceed 500 characters")
    private String mainImageUrl;

    private List<String> imageUrls;

    @DecimalMin(value = "0.00", message = "Weight must be at least 0.00")
    private BigDecimal weight;

    @Size(max = 10, message = "Weight unit must not exceed 10 characters")
    private String weightUnit;

    @DecimalMin(value = "0.00", message = "Length must be at least 0.00")
    private BigDecimal length;

    @DecimalMin(value = "0.00", message = "Width must be at least 0.00")
    private BigDecimal width;

    @DecimalMin(value = "0.00", message = "Height must be at least 0.00")
    private BigDecimal height;

    @Size(max = 10, message = "Dimension unit must not exceed 10 characters")
    private String dimensionUnit;

    private Boolean isFeatured;

    private Boolean isDigital;

    @Size(max = 200, message = "Meta title must not exceed 200 characters")
    private String metaTitle;

    @Size(max = 500, message = "Meta description must not exceed 500 characters")
    private String metaDescription;
}
