package site.clickbasketecom.ClickBasket.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;

    // Items
    private List<OrderItemResponse> items;
    private Integer totalItems;

    // Pricing
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String couponCode;

    // Shipping address
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZip;
    private String shippingCountry;

    // Billing address
    private String billingName;
    private String billingPhone;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingZip;
    private String billingCountry;

    // Additional
    private String notes;
    private String trackingNumber;
    private String shippingCarrier;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
