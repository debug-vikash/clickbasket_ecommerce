package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.OrderItem;

import java.util.List;

/**
 * Repository for OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find order items by order ID.
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find order items by vendor ID.
     */
    Page<OrderItem> findByVendorId(Long vendorId, Pageable pageable);

    /**
     * Find order items by vendor ID and fulfillment status.
     */
    Page<OrderItem> findByVendorIdAndFulfillmentStatus(Long vendorId, OrderItem.FulfillmentStatus status,
            Pageable pageable);
}
