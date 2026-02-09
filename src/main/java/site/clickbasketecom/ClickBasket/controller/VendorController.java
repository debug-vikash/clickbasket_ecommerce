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
import site.clickbasketecom.ClickBasket.dto.vendor.VendorRegisterRequest;
import site.clickbasketecom.ClickBasket.dto.vendor.VendorResponse;
import site.clickbasketecom.ClickBasket.dto.vendor.VendorUpdateRequest;
import site.clickbasketecom.ClickBasket.entity.Vendor;
import site.clickbasketecom.ClickBasket.security.CustomUserDetails;
import site.clickbasketecom.ClickBasket.service.VendorService;

/**
 * REST controller for vendor management.
 */
@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor Management", description = "Vendor onboarding and management operations")
@SecurityRequirement(name = "bearerAuth")
public class VendorController {

    private final VendorService vendorService;

    // ========================
    // Public Vendor Endpoints
    // ========================

    @Operation(summary = "Get vendor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Vendor not found")
    })
    @GetMapping("/{vendorId}")
    public ResponseEntity<VendorResponse> getVendorById(@PathVariable Long vendorId) {
        VendorResponse response = vendorService.getVendorById(vendorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all approved vendors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendors retrieved successfully")
    })
    @GetMapping("/approved")
    public ResponseEntity<Page<VendorResponse>> getApprovedVendors(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VendorResponse> vendors = vendorService.getVendorsByStatus(Vendor.VendorStatus.APPROVED, pageable);
        return ResponseEntity.ok(vendors);
    }

    // ========================
    // Vendor Self-Service
    // ========================

    @Operation(summary = "Register as vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vendor registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Already a vendor or store name taken")
    })
    @PostMapping("/register")
    public ResponseEntity<VendorResponse> registerVendor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VendorRegisterRequest request) {
        VendorResponse response = vendorService.registerVendor(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get my vendor profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Not registered as vendor")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VendorResponse> getMyVendorProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        VendorResponse response = vendorService.getVendorByUserId(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update my vendor profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Not registered as vendor"),
            @ApiResponse(responseCode = "409", description = "Store name already taken")
    })
    @PutMapping("/me")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VendorResponse> updateMyVendorProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VendorUpdateRequest request) {
        VendorResponse response = vendorService.updateVendorByUserId(userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    // ========================
    // Admin Vendor Management
    // ========================

    @Operation(summary = "Get all vendors (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VendorResponse>> getAllVendors(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VendorResponse> vendors = vendorService.getAllVendors(pageable);
        return ResponseEntity.ok(vendors);
    }

    @Operation(summary = "Get vendors by status (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VendorResponse>> getVendorsByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Vendor.VendorStatus vendorStatus = Vendor.VendorStatus.valueOf(status.toUpperCase());
        Page<VendorResponse> vendors = vendorService.getVendorsByStatus(vendorStatus, pageable);
        return ResponseEntity.ok(vendors);
    }

    @Operation(summary = "Approve a vendor (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor approved successfully"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{vendorId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorResponse> approveVendor(@PathVariable Long vendorId) {
        VendorResponse response = vendorService.approveVendor(vendorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reject a vendor (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{vendorId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorResponse> rejectVendor(@PathVariable Long vendorId) {
        VendorResponse response = vendorService.rejectVendor(vendorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Suspend a vendor (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor suspended successfully"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{vendorId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorResponse> suspendVendor(@PathVariable Long vendorId) {
        VendorResponse response = vendorService.suspendVendor(vendorId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update vendor profile (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Vendor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{vendorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorResponse> updateVendor(
            @PathVariable Long vendorId,
            @Valid @RequestBody VendorUpdateRequest request) {
        VendorResponse response = vendorService.updateVendor(vendorId, request);
        return ResponseEntity.ok(response);
    }
}
