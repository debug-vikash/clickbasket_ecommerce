package site.clickbasketecom.ClickBasket.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for product response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private String shortDescription;

    // Vendor info
    private Long vendorId;
    private String vendorName;
    private String storeName;

    // Category info
    private Long categoryId;
    private String categoryName;

    // Pricing
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal costPrice;
    private BigDecimal discountPercentage;

    // Stock
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean inStock;
    private Boolean lowStock;

    // Images
    private String mainImageUrl;
    private List<String> imageUrls;

    // Dimensions
    private BigDecimal weight;
    private String weightUnit;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUnit;

    // Status & flags
    private String status;
    private Boolean isFeatured;
    private Boolean isDigital;

    // Stats
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer soldCount;
    private Integer viewCount;

    // SEO
    private String metaTitle;
    private String metaDescription;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
