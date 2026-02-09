package site.clickbasketecom.ClickBasket.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String transactionId;
    private String paymentMethod;
    private String paymentProvider;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private BigDecimal refundAmount;
    private String refundReason;
    private String failureReason;
    private String cardLastFour;
    private String cardBrand;
    private String billingEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
