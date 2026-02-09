package site.clickbasketecom.ClickBasket.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for initiating a payment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    @Size(max = 50, message = "Payment provider must not exceed 50 characters")
    private String paymentProvider;

    // Optional card details (for simulation)
    @Size(max = 4, message = "Card last four must be 4 characters")
    private String cardLastFour;

    @Size(max = 20, message = "Card brand must not exceed 20 characters")
    private String cardBrand;

    @Size(max = 150, message = "Billing email must not exceed 150 characters")
    private String billingEmail;
}
