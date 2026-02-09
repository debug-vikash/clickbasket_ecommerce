package site.clickbasketecom.ClickBasket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.clickbasketecom.ClickBasket.dto.payment.ConfirmPaymentRequest;
import site.clickbasketecom.ClickBasket.dto.payment.InitiatePaymentRequest;
import site.clickbasketecom.ClickBasket.dto.payment.PaymentResponse;
import site.clickbasketecom.ClickBasket.security.CustomUserDetails;
import site.clickbasketecom.ClickBasket.service.PaymentService;

/**
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment processing operations")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Initiate a payment for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or payment already exists"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InitiatePaymentRequest request) {
        PaymentResponse response = paymentService.initiatePayment(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Confirm payment result (gateway callback simulation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment confirmed"),
            @ApiResponse(responseCode = "400", description = "Invalid payment status"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequest request) {
        PaymentResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment by order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(userDetails.getUserId(), orderId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment by transaction ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        PaymentResponse response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }

    // ========================
    // Simulation Endpoints (for testing)
    // ========================

    @Operation(summary = "Simulate successful payment (for testing)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment simulated as successful"),
            @ApiResponse(responseCode = "400", description = "Payment not in pending status"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{paymentId}/simulate/success")
    public ResponseEntity<PaymentResponse> simulateSuccessfulPayment(
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.simulateSuccessfulPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Simulate failed payment (for testing)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment simulated as failed"),
            @ApiResponse(responseCode = "400", description = "Payment not in pending status"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{paymentId}/simulate/failure")
    public ResponseEntity<PaymentResponse> simulateFailedPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason) {
        PaymentResponse response = paymentService.simulateFailedPayment(paymentId, reason);
        return ResponseEntity.ok(response);
    }
}
