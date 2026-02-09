package site.clickbasketecom.ClickBasket.dto.vendor;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating vendor profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorUpdateRequest {

    @Size(min = 2, max = 150, message = "Store name must be between 2 and 150 characters")
    private String storeName;

    @Size(max = 2000, message = "Store description must not exceed 2000 characters")
    private String storeDescription;

    @Size(max = 500, message = "Store logo URL must not exceed 500 characters")
    private String storeLogoUrl;

    @Size(max = 500, message = "Store banner URL must not exceed 500 characters")
    private String storeBannerUrl;

    @Size(max = 150, message = "Business email must not exceed 150 characters")
    private String businessEmail;

    @Size(max = 20, message = "Business phone must not exceed 20 characters")
    private String businessPhone;

    @Size(max = 500, message = "Business address must not exceed 500 characters")
    private String businessAddress;

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;
}
