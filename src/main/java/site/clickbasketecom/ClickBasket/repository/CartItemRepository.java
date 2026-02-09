package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.CartItem;

import java.util.Optional;

/**
 * Repository for CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find cart item by cart ID and product ID.
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Check if item exists in cart.
     */
    Boolean existsByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete cart item by cart ID and product ID.
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
