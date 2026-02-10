package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.product.CreateProductRequest;
import site.clickbasketecom.ClickBasket.dto.product.ProductResponse;
import site.clickbasketecom.ClickBasket.dto.product.UpdateProductRequest;
import site.clickbasketecom.ClickBasket.entity.Category;
import site.clickbasketecom.ClickBasket.entity.Product;
import site.clickbasketecom.ClickBasket.entity.Vendor;
import site.clickbasketecom.ClickBasket.exception.CategoryNotFoundException;
import site.clickbasketecom.ClickBasket.exception.ProductNotFoundException;
import site.clickbasketecom.ClickBasket.repository.CategoryRepository;
import site.clickbasketecom.ClickBasket.repository.ProductRepository;
import site.clickbasketecom.ClickBasket.repository.VendorRepository;

/**
 * Service for product management operations.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final VendorService vendorService;

    // ========================
    // Vendor Product Operations
    // ========================

    /**
     * Create a new product (vendor only).
     */
    @Transactional
    public ProductResponse createProduct(Long userId, CreateProductRequest request) {
        // Ensure vendor is approved
        Vendor vendor = vendorService.ensureVendorApprovedByUserId(userId);

        // Validate category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        // Validate SKU uniqueness
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }

        // Generate slug
        String slug = generateSlug(request.getName());
        int counter = 1;
        String originalSlug = slug;
        while (productRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter++;
        }

        Product product = Product.builder()
                .vendor(vendor)
                .category(category)
                .name(request.getName())
                .slug(slug)
                .sku(request.getSku())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
                .mainImageUrl(request.getMainImageUrl())
                .imageUrls(request.getImageUrls() != null ? request.getImageUrls() : new java.util.ArrayList<>())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit() != null ? request.getWeightUnit() : "kg")
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .dimensionUnit(request.getDimensionUnit() != null ? request.getDimensionUnit() : "cm")
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .isDigital(request.getIsDigital() != null ? request.getIsDigital() : false)
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .status(Product.ProductStatus.DRAFT)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    /**
     * Update a product (vendor only, own products).
     */
    @Transactional
    public ProductResponse updateProduct(Long userId, Long productId, UpdateProductRequest request) {
        // Ensure vendor is approved
        Vendor vendor = vendorService.ensureVendorApprovedByUserId(userId);

        // Find product and verify ownership
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getVendor().getId().equals(vendor.getId())) {
            throw new IllegalArgumentException("You can only update your own products");
        }

        // Update fields
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new IllegalArgumentException("SKU already exists: " + request.getSku());
            }
            product.setSku(request.getSku());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getCompareAtPrice() != null) {
            product.setCompareAtPrice(request.getCompareAtPrice());
        }

        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        if (request.getLowStockThreshold() != null) {
            product.setLowStockThreshold(request.getLowStockThreshold());
        }

        if (request.getMainImageUrl() != null) {
            product.setMainImageUrl(request.getMainImageUrl());
        }

        if (request.getImageUrls() != null) {
            product.setImageUrls(request.getImageUrls());
        }

        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }

        if (request.getWeightUnit() != null) {
            product.setWeightUnit(request.getWeightUnit());
        }

        if (request.getLength() != null) {
            product.setLength(request.getLength());
        }

        if (request.getWidth() != null) {
            product.setWidth(request.getWidth());
        }

        if (request.getHeight() != null) {
            product.setHeight(request.getHeight());
        }

        if (request.getDimensionUnit() != null) {
            product.setDimensionUnit(request.getDimensionUnit());
        }

        if (request.getStatus() != null) {
            product.setStatus(Product.ProductStatus.valueOf(request.getStatus().toUpperCase()));
        }

        if (request.getIsFeatured() != null) {
            product.setIsFeatured(request.getIsFeatured());
        }

        if (request.getIsDigital() != null) {
            product.setIsDigital(request.getIsDigital());
        }

        if (request.getMetaTitle() != null) {
            product.setMetaTitle(request.getMetaTitle());
        }

        if (request.getMetaDescription() != null) {
            product.setMetaDescription(request.getMetaDescription());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    /**
     * Delete a product (vendor only, own products).
     */
    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        // Ensure vendor is approved
        Vendor vendor = vendorService.ensureVendorApprovedByUserId(userId);

        // Find product and verify ownership
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getVendor().getId().equals(vendor.getId())) {
            throw new IllegalArgumentException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    /**
     * Get vendor's own products.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getVendorProducts(Long userId, Pageable pageable) {
        Vendor vendor = vendorService.ensureVendorApprovedByUserId(userId);
        return productRepository.findByVendorId(vendor.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get vendor's product by ID.
     */
    @Transactional(readOnly = true)
    public ProductResponse getVendorProduct(Long userId, Long productId) {
        Vendor vendor = vendorService.ensureVendorApprovedByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getVendor().getId().equals(vendor.getId())) {
            throw new IllegalArgumentException("You can only view your own products");
        }

        return mapToResponse(product);
    }

    // ========================
    // Public Product Operations
    // ========================

    /**
     * Get all active products (public).
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get product by ID (public, only active).
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // Only allow viewing active products publicly
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new ProductNotFoundException(productId);
        }

        // Increment view count
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return mapToResponse(product);
    }

    /**
     * Get product by slug (public, only active).
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with slug: " + slug));

        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new ProductNotFoundException("Product not found with slug: " + slug);
        }

        return mapToResponse(product);
    }

    /**
     * Get products by category (public, only active).
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndStatus(categoryId, Product.ProductStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get featured products (public).
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByStatusAndIsFeaturedTrue(Product.ProductStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Search products by name (public, only active).
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.findByStatusAndNameContainingIgnoreCase(Product.ProductStatus.ACTIVE, query, pageable)
                .map(this::mapToResponse);
    }

    // ========================
    // Helper Methods
    // ========================

    /**
     * Map Product entity to ProductResponse DTO.
     */
    private ProductResponse mapToResponse(Product product) {
        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .costPrice(product.getCostPrice())
                .discountPercentage(product.getDiscountPercentage())
                .stockQuantity(product.getStockQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .inStock(product.isInStock())
                .lowStock(product.isLowStock())
                .mainImageUrl(product.getMainImageUrl())
                .imageUrls(product.getImageUrls())
                .weight(product.getWeight())
                .weightUnit(product.getWeightUnit())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .dimensionUnit(product.getDimensionUnit())
                .status(product.getStatus().name())
                .isFeatured(product.getIsFeatured())
                .isDigital(product.getIsDigital())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .soldCount(product.getSoldCount())
                .viewCount(product.getViewCount())
                .metaTitle(product.getMetaTitle())
                .metaDescription(product.getMetaDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt());

        // Add vendor info
        if (product.getVendor() != null) {
            builder.vendorId(product.getVendor().getId())
                    .vendorName(product.getVendor().getUser().getFirstName() + " "
                            + product.getVendor().getUser().getLastName())
                    .storeName(product.getVendor().getStoreName());
        }

        // Add category info
        if (product.getCategory() != null) {
            builder.categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName());
        }

        return builder.build();
    }

    /**
     * Generate a URL-friendly slug from a name.
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
