package site.clickbasketecom.ClickBasket.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for placing an order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    // Shipping Address
    @NotBlank(message = "Shipping name is required")
    @Size(max = 200, message = "Shipping name must not exceed 200 characters")
    private String shippingName;

    @NotBlank(message = "Shipping phone is required")
    @Size(max = 20, message = "Shipping phone must not exceed 20 characters")
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;

    @NotBlank(message = "Shipping state is required")
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;

    @NotBlank(message = "Shipping ZIP is required")
    @Size(max = 20, message = "Shipping ZIP must not exceed 20 characters")
    private String shippingZip;

    @NotBlank(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;

    // Optional billing address (uses shipping if not provided)
    @Size(max = 200, message = "Billing name must not exceed 200 characters")
    private String billingName;

    @Size(max = 20, message = "Billing phone must not exceed 20 characters")
    private String billingPhone;

    @Size(max = 500, message = "Billing address must not exceed 500 characters")
    private String billingAddress;

    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    private String billingCity;

    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    private String billingState;

    @Size(max = 20, message = "Billing ZIP must not exceed 20 characters")
    private String billingZip;

    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    private String billingCountry;

    // Additional
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
