package site.clickbasketecom.ClickBasket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.clickbasketecom.ClickBasket.dto.product.CreateProductRequest;
import site.clickbasketecom.ClickBasket.dto.product.ProductResponse;
import site.clickbasketecom.ClickBasket.dto.product.UpdateProductRequest;
import site.clickbasketecom.ClickBasket.security.CustomUserDetails;
import site.clickbasketecom.ClickBasket.service.ProductService;

/**
 * REST controller for product management.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Product CRUD and listing operations")
public class ProductController {

    private final ProductService productService;

    // ========================
    // Public Product Endpoints
    // ========================

    @Operation(summary = "Get all active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.getActiveProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        ProductResponse response = productService.getProductBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get featured products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured products retrieved successfully")
    })
    @GetMapping("/featured")
    public ResponseEntity<Page<ProductResponse>> getFeaturedProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Search products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(products);
    }

    // ========================
    // Vendor Product Endpoints
    // ========================

    @Operation(summary = "Get my products (Vendor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Vendor not approved")
    })
    @GetMapping("/vendor/me")
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.getVendorProducts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get my product by ID (Vendor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Vendor not approved or not owner"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/vendor/me/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponse> getMyProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        ProductResponse response = productService.getVendorProduct(userDetails.getUserId(), productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new product (Vendor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Vendor not approved")
    })
    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a product (Vendor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Vendor not approved or not owner"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(userDetails.getUserId(), productId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a product (Vendor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Vendor not approved or not owner"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        productService.deleteProduct(userDetails.getUserId(), productId);
        return ResponseEntity.noContent().build();
    }
}
