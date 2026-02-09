package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Payment;

import java.util.Optional;

/**
 * Repository for Payment entity.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by order ID.
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Find payment by transaction ID.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Check if payment exists for order.
     */
    Boolean existsByOrderId(Long orderId);

    /**
     * Find payments by status.
     */
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);
}
