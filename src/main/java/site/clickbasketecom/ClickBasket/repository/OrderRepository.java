package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Order;

import java.util.Optional;

/**
 * Repository for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by user ID.
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Find orders by user ID and status.
     */
    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);

    /**
     * Find order by order number.
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by status.
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Check if order number exists.
     */
    Boolean existsByOrderNumber(String orderNumber);
}
