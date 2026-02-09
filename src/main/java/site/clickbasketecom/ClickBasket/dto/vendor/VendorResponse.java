package site.clickbasketecom.ClickBasket.dto.vendor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for vendor response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorResponse {

    private Long id;
    private Long userId;
    private String ownerName;
    private String ownerEmail;
    private String storeName;
    private String storeDescription;
    private String storeLogoUrl;
    private String storeBannerUrl;
    private String businessEmail;
    private String businessPhone;
    private String businessAddress;
    private String taxId;
    private BigDecimal commissionRate;
    private String status;
    private Boolean verified;
    private BigDecimal rating;
    private Integer totalReviews;
    private Integer totalProducts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
