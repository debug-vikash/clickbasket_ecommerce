package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.order.OrderItemResponse;
import site.clickbasketecom.ClickBasket.dto.order.OrderResponse;
import site.clickbasketecom.ClickBasket.dto.order.PlaceOrderRequest;
import site.clickbasketecom.ClickBasket.entity.*;
import site.clickbasketecom.ClickBasket.exception.OrderNotFoundException;
import site.clickbasketecom.ClickBasket.exception.UserNotFoundException;
import site.clickbasketecom.ClickBasket.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for order management operations.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Place an order from the user's cart.
     */
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart is empty"));

        // Validate cart has items
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Validate stock for all items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                throw new IllegalStateException("Product is not available: " + product.getName());
            }
        }

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Create order
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .status(Order.OrderStatus.PENDING)
                .subtotal(cart.getSubtotal())
                .discountAmount(cart.getDiscountAmount())
                .couponCode(cart.getCouponCode())
                .shippingName(request.getShippingName())
                .shippingPhone(request.getShippingPhone())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingZip(request.getShippingZip())
                .shippingCountry(request.getShippingCountry())
                .billingName(request.getBillingName() != null ? request.getBillingName() : request.getShippingName())
                .billingPhone(
                        request.getBillingPhone() != null ? request.getBillingPhone() : request.getShippingPhone())
                .billingAddress(request.getBillingAddress() != null ? request.getBillingAddress()
                        : request.getShippingAddress())
                .billingCity(request.getBillingCity() != null ? request.getBillingCity() : request.getShippingCity())
                .billingState(
                        request.getBillingState() != null ? request.getBillingState() : request.getShippingState())
                .billingZip(request.getBillingZip() != null ? request.getBillingZip() : request.getShippingZip())
                .billingCountry(request.getBillingCountry() != null ? request.getBillingCountry()
                        : request.getShippingCountry())
                .notes(request.getNotes())
                .build();

        // Create order items from cart items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .vendor(product.getVendor())
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .productImageUrl(product.getMainImageUrl())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .fulfillmentStatus(OrderItem.FulfillmentStatus.PENDING)
                    .build();

            order.getItems().add(orderItem);

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSoldCount(product.getSoldCount() + cartItem.getQuantity());
            productRepository.save(product);
        }

        // Calculate totals
        order.calculateTotals();

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.clearCart();
        cartRepository.save(cart);

        // Send email notifications
        emailService.sendOrderConfirmationEmail(savedOrder);
        emailService.notifyVendorsAboutOrder(savedOrder);

        return mapToResponse(savedOrder);
    }

    /**
     * Get user's orders.
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get order by ID (user must own the order).
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own orders");
        }

        return mapToResponse(order);
    }

    /**
     * Get order by order number (user must own the order).
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(Long userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own orders");
        }

        return mapToResponse(order);
    }

    /**
     * Cancel an order (only if status is PENDING or CONFIRMED).
     */
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only cancel your own orders");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setSoldCount(product.getSoldCount() - item.getQuantity());
            productRepository.save(product);

            item.setFulfillmentStatus(OrderItem.FulfillmentStatus.CANCELLED);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    // ========================
    // Admin Operations
    // ========================

    /**
     * Update order status (admin only).
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getStatus() == Order.OrderStatus.DELIVERED) {
            emailService.sendOrderDeliveredEmail(savedOrder);
        }

        return mapToResponse(savedOrder);
    }

    /**
     * Get all orders (admin only).
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get orders by status (admin only).
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(Order.OrderStatus.valueOf(status.toUpperCase()), pageable)
                .map(this::mapToResponse);
    }

    // ========================
    // Helper Methods
    // ========================

    /**
     * Generate a unique order number.
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    /**
     * Map Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        int totalItems = order.getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .items(items)
                .totalItems(totalItems)
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .couponCode(order.getCouponCode())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingZip(order.getShippingZip())
                .shippingCountry(order.getShippingCountry())
                .billingName(order.getBillingName())
                .billingPhone(order.getBillingPhone())
                .billingAddress(order.getBillingAddress())
                .billingCity(order.getBillingCity())
                .billingState(order.getBillingState())
                .billingZip(order.getBillingZip())
                .billingCountry(order.getBillingCountry())
                .notes(order.getNotes())
                .trackingNumber(order.getTrackingNumber())
                .shippingCarrier(order.getShippingCarrier())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Map OrderItem entity to OrderItemResponse DTO.
     */
    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .productImageUrl(item.getProductImageUrl())
                .vendorId(item.getVendor().getId())
                .storeName(item.getVendor().getStoreName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountAmount(item.getDiscountAmount())
                .taxAmount(item.getTaxAmount())
                .totalPrice(item.getTotalPrice())
                .fulfillmentStatus(item.getFulfillmentStatus().name())
                .build();
    }
}
