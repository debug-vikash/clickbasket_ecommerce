package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Cart;

import java.util.Optional;

/**
 * Repository for Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find cart by user ID.
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Check if cart exists for user.
     */
    Boolean existsByUserId(Long userId);
}
