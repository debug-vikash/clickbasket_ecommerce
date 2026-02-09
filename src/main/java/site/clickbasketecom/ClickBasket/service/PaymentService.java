package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.payment.ConfirmPaymentRequest;
import site.clickbasketecom.ClickBasket.dto.payment.InitiatePaymentRequest;
import site.clickbasketecom.ClickBasket.dto.payment.PaymentResponse;
import site.clickbasketecom.ClickBasket.entity.Order;
import site.clickbasketecom.ClickBasket.entity.Payment;
import site.clickbasketecom.ClickBasket.exception.OrderNotFoundException;
import site.clickbasketecom.ClickBasket.exception.PaymentNotFoundException;
import site.clickbasketecom.ClickBasket.repository.OrderRepository;
import site.clickbasketecom.ClickBasket.repository.PaymentRepository;

import java.time.LocalDateTime;

/**
 * Service for payment operations.
 * Simulates real-world payment flow without actual gateway integration.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * Initiate a payment for an order.
     */
    @Transactional
    public PaymentResponse initiatePayment(Long userId, InitiatePaymentRequest request) {
        // Find order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

        // Verify user owns the order
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only pay for your own orders");
        }

        // Check if order is in valid status for payment
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in a valid status for payment: " + order.getStatus());
        }

        // Check if payment already exists
        if (paymentRepository.existsByOrderId(order.getId())) {
            Payment existingPayment = paymentRepository.findByOrderId(order.getId())
                    .orElseThrow();

            // If previous payment failed, allow retry
            if (existingPayment.getStatus() != Payment.PaymentStatus.FAILED &&
                    existingPayment.getStatus() != Payment.PaymentStatus.CANCELLED) {
                throw new IllegalStateException(
                        "Payment already exists for this order with status: " + existingPayment.getStatus());
            }

            // Update existing payment for retry
            existingPayment.setPaymentMethod(request.getPaymentMethod());
            existingPayment.setPaymentProvider(request.getPaymentProvider());
            existingPayment.setStatus(Payment.PaymentStatus.PENDING);
            existingPayment.setCardLastFour(request.getCardLastFour());
            existingPayment.setCardBrand(request.getCardBrand());
            existingPayment.setBillingEmail(request.getBillingEmail());
            existingPayment.setFailureReason(null);

            Payment savedPayment = paymentRepository.save(existingPayment);
            return mapToResponse(savedPayment);
        }

        // Create new payment
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentProvider(request.getPaymentProvider())
                .amount(order.getTotalAmount())
                .currency("INR")
                .status(Payment.PaymentStatus.PENDING)
                .cardLastFour(request.getCardLastFour())
                .cardBrand(request.getCardBrand())
                .billingEmail(request.getBillingEmail())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update order status to processing
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return mapToResponse(savedPayment);
    }

    /**
     * Confirm payment success or failure (simulating gateway callback).
     */
    @Transactional
    public PaymentResponse confirmPayment(ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException(request.getPaymentId()));

        // Payment must be pending
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status: " + payment.getStatus());
        }

        Order order = payment.getOrder();

        if (request.getSuccess()) {
            // Payment successful
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId(request.getTransactionId());
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayResponse(request.getGatewayResponse());

            // Update order status
            order.setStatus(Order.OrderStatus.PROCESSING);
        } else {
            // Payment failed
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setTransactionId(request.getTransactionId());
            payment.setFailureReason(request.getFailureReason());
            payment.setGatewayResponse(request.getGatewayResponse());

            // Revert order status
            order.setStatus(Order.OrderStatus.PENDING);
        }

        orderRepository.save(order);
        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }

    /**
     * Get payment by order ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view payments for your own orders");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));

        return mapToResponse(payment);
    }

    /**
     * Get payment by transaction ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for transaction: " + transactionId));

        return mapToResponse(payment);
    }

    /**
     * Simulate a successful payment (for testing).
     */
    @Transactional
    public PaymentResponse simulateSuccessfulPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        // Generate simulated transaction ID
        String transactionId = "SIM-" + System.currentTimeMillis() + "-" + payment.getId();

        ConfirmPaymentRequest confirmRequest = ConfirmPaymentRequest.builder()
                .paymentId(paymentId)
                .transactionId(transactionId)
                .success(true)
                .gatewayResponse("{\"status\":\"success\",\"simulated\":true}")
                .build();

        return confirmPayment(confirmRequest);
    }

    /**
     * Simulate a failed payment (for testing).
     */
    @Transactional
    public PaymentResponse simulateFailedPayment(Long paymentId, String failureReason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        // Generate simulated transaction ID
        String transactionId = "SIM-FAIL-" + System.currentTimeMillis() + "-" + payment.getId();

        ConfirmPaymentRequest confirmRequest = ConfirmPaymentRequest.builder()
                .paymentId(paymentId)
                .transactionId(transactionId)
                .success(false)
                .failureReason(failureReason != null ? failureReason : "Simulated payment failure")
                .gatewayResponse("{\"status\":\"failed\",\"simulated\":true}")
                .build();

        return confirmPayment(confirmRequest);
    }

    // ========================
    // Helper Methods
    // ========================

    /**
     * Map Payment entity to PaymentResponse DTO.
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentProvider(payment.getPaymentProvider())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .refundAmount(payment.getRefundAmount())
                .refundReason(payment.getRefundReason())
                .failureReason(payment.getFailureReason())
                .cardLastFour(payment.getCardLastFour())
                .cardBrand(payment.getCardBrand())
                .billingEmail(payment.getBillingEmail())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
