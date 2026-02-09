package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by vendor ID.
     */
    Page<Product> findByVendorId(Long vendorId, Pageable pageable);

    /**
     * Find all active products by vendor ID.
     */
    Page<Product> findByVendorIdAndStatus(Long vendorId, Product.ProductStatus status, Pageable pageable);

    /**
     * Find all products by category ID.
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find all active products by category ID.
     */
    Page<Product> findByCategoryIdAndStatus(Long categoryId, Product.ProductStatus status, Pageable pageable);

    /**
     * Find all active products.
     */
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

    /**
     * Find product by slug.
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Find product by SKU.
     */
    Optional<Product> findBySku(String sku);

    /**
     * Check if SKU exists.
     */
    Boolean existsBySku(String sku);

    /**
     * Check if slug exists.
     */
    Boolean existsBySlug(String slug);

    /**
     * Find featured products.
     */
    Page<Product> findByStatusAndIsFeaturedTrue(Product.ProductStatus status, Pageable pageable);

    /**
     * Count products by vendor.
     */
    Long countByVendorId(Long vendorId);

    /**
     * Count active products by vendor.
     */
    Long countByVendorIdAndStatus(Long vendorId, Product.ProductStatus status);

    /**
     * Find products by vendor and category.
     */
    Page<Product> findByVendorIdAndCategoryId(Long vendorId, Long categoryId, Pageable pageable);

    /**
     * Search products by name.
     */
    Page<Product> findByStatusAndNameContainingIgnoreCase(Product.ProductStatus status, String name, Pageable pageable);
}
