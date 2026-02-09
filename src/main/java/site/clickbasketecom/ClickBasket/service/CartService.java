package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.cart.AddToCartRequest;
import site.clickbasketecom.ClickBasket.dto.cart.CartItemResponse;
import site.clickbasketecom.ClickBasket.dto.cart.CartResponse;
import site.clickbasketecom.ClickBasket.dto.cart.UpdateCartItemRequest;
import site.clickbasketecom.ClickBasket.entity.Cart;
import site.clickbasketecom.ClickBasket.entity.CartItem;
import site.clickbasketecom.ClickBasket.entity.Product;
import site.clickbasketecom.ClickBasket.entity.User;
import site.clickbasketecom.ClickBasket.exception.ProductNotFoundException;
import site.clickbasketecom.ClickBasket.exception.UserNotFoundException;
import site.clickbasketecom.ClickBasket.repository.CartItemRepository;
import site.clickbasketecom.ClickBasket.repository.CartRepository;
import site.clickbasketecom.ClickBasket.repository.ProductRepository;
import site.clickbasketecom.ClickBasket.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for shopping cart operations.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Get user's cart. Creates one if it doesn't exist.
     */
    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    /**
     * Add item to cart.
     */
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        // Find product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        // Check if product is active
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new IllegalStateException("Product is not available for purchase");
        }

        // Check stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock. Available: " + product.getStockQuantity());
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Total quantity exceeds available stock. Available: " + product.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setUnitPrice(product.getPrice()); // Update to current price
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
            cartRepository.save(cart);
        }

        return mapToResponse(cart);
    }

    /**
     * Update cart item quantity.
     */
    @Transactional
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        // Find cart item
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        // Check stock availability
        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock. Available: " + product.getStockQuantity());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setUnitPrice(product.getPrice()); // Update to current price
        cartItemRepository.save(cartItem);

        return mapToResponse(cart);
    }

    /**
     * Remove item from cart.
     */
    @Transactional
    public CartResponse removeFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        // Find and remove cart item
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return mapToResponse(cart);
    }

    /**
     * Clear all items from cart.
     */
    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clearCart();
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    // ========================
    // Helper Methods
    // ========================

    /**
     * Get or create a cart for the user.
     */
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Map Cart entity to CartResponse DTO.
     */
    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(cart.getTotalItems())
                .uniqueItems(items.size())
                .subtotal(cart.getSubtotal())
                .couponCode(cart.getCouponCode())
                .discountAmount(cart.getDiscountAmount())
                .total(cart.getTotal())
                .build();
    }

    /**
     * Map CartItem entity to CartItemResponse DTO.
     */
    private CartItemResponse mapItemToResponse(CartItem item) {
        Product product = item.getProduct();
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .productImage(product.getMainImageUrl())
                .vendorId(product.getVendor() != null ? product.getVendor().getId() : null)
                .storeName(product.getVendor() != null ? product.getVendor().getStoreName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .availableStock(product.getStockQuantity())
                .inStock(product.isInStock())
                .build();
    }
}
