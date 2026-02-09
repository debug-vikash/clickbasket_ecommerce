package site.clickbasketecom.ClickBasket.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for confirming payment (simulating gateway callback).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    /**
     * true = payment success, false = payment failed
     */
    @NotNull(message = "Success status is required")
    private Boolean success;

    // Optional failure reason
    private String failureReason;

    // Optional gateway response (JSON string)
    private String gatewayResponse;
}
