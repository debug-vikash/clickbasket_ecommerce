package site.clickbasketecom.ClickBasket.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import site.clickbasketecom.ClickBasket.entity.Order;
import site.clickbasketecom.ClickBasket.entity.OrderItem;
import site.clickbasketecom.ClickBasket.entity.Vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for sending email notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from-address:noreply@clickbasket.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:ClickBasket}")
    private String fromName;

    /**
     * Send order confirmation email to customer.
     */
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            String customerEmail = order.getUser().getEmail();
            String customerName = order.getUser().getFirstName() != null
                    ? order.getUser().getFirstName()
                    : order.getUser().getEmail();

            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", customerName);
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("orderDate", order.getCreatedAt());
            variables.put("items", order.getItems());
            variables.put("subtotal", order.getSubtotal());
            variables.put("shippingCost", order.getShippingCost());
            variables.put("taxAmount", order.getTaxAmount());
            variables.put("discountAmount", order.getDiscountAmount());
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("shippingAddress", formatShippingAddress(order));

            String subject = "Order Confirmation - " + order.getOrderNumber();
            String htmlContent = processTemplate("order-confirmation", variables);

            sendHtmlEmail(customerEmail, subject, htmlContent);
            log.info("Order confirmation email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Failed to send order confirmation email for order: {}", order.getOrderNumber(), e);
        }
    }

    /**
     * Send new order notification to vendor.
     */
    @Async
    public void sendVendorOrderNotificationEmail(Order order, Vendor vendor, List<OrderItem> vendorItems) {
        try {
            String vendorEmail = vendor.getBusinessEmail() != null
                    ? vendor.getBusinessEmail()
                    : vendor.getUser().getEmail();

            Map<String, Object> variables = new HashMap<>();
            variables.put("storeName", vendor.getStoreName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("orderDate", order.getCreatedAt());
            variables.put("items", vendorItems);
            variables.put("customerName", order.getShippingName());
            variables.put("shippingAddress", formatShippingAddress(order));
            variables.put("itemTotal", vendorItems.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

            String subject = "New Order Received - " + order.getOrderNumber();
            String htmlContent = processTemplate("vendor-order-notification", variables);

            sendHtmlEmail(vendorEmail, subject, htmlContent);
            log.info("Vendor order notification sent to: {} for order: {}", vendorEmail, order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to send vendor notification for order: {}", order.getOrderNumber(), e);
        }
    }

    /**
     * Send order delivered notification to customer.
     */
    @Async
    public void sendOrderDeliveredEmail(Order order) {
        try {
            String customerEmail = order.getUser().getEmail();
            String customerName = order.getUser().getFirstName() != null
                    ? order.getUser().getFirstName()
                    : order.getUser().getEmail();

            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", customerName);
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("deliveryDate", java.time.LocalDateTime.now());
            variables.put("items", order.getItems());
            variables.put("totalAmount", order.getTotalAmount());

            String subject = "Your Order Has Been Delivered - " + order.getOrderNumber();
            String htmlContent = processTemplate("order-delivered", variables);

            sendHtmlEmail(customerEmail, subject, htmlContent);
            log.info("Order delivered email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Failed to send order delivered email for order: {}", order.getOrderNumber(), e);
        }
    }

    /**
     * Notify all vendors about an order.
     */
    @Async
    public void notifyVendorsAboutOrder(Order order) {
        // Group items by vendor
        Map<Vendor, List<OrderItem>> itemsByVendor = order.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getVendor));

        // Send notification to each vendor
        for (Map.Entry<Vendor, List<OrderItem>> entry : itemsByVendor.entrySet()) {
            sendVendorOrderNotificationEmail(order, entry.getKey(), entry.getValue());
        }
    }

    // ========================
    // Helper Methods
    // ========================

    /**
     * Send HTML email.
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Process Thymeleaf template.
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process("email/" + templateName, context);
    }

    /**
     * Format shipping address for display.
     */
    private String formatShippingAddress(Order order) {
        return String.format("%s, %s, %s, %s - %s, %s",
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingCountry(),
                order.getShippingZip(),
                order.getShippingPhone());
    }
}
