package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.vendor.VendorRegisterRequest;
import site.clickbasketecom.ClickBasket.dto.vendor.VendorResponse;
import site.clickbasketecom.ClickBasket.dto.vendor.VendorUpdateRequest;
import site.clickbasketecom.ClickBasket.entity.Role;
import site.clickbasketecom.ClickBasket.entity.User;
import site.clickbasketecom.ClickBasket.entity.Vendor;
import site.clickbasketecom.ClickBasket.exception.EmailAlreadyExistsException;
import site.clickbasketecom.ClickBasket.exception.RoleNotFoundException;
import site.clickbasketecom.ClickBasket.exception.UserNotFoundException;
import site.clickbasketecom.ClickBasket.exception.VendorNotFoundException;
import site.clickbasketecom.ClickBasket.exception.VendorNotApprovedException;
import site.clickbasketecom.ClickBasket.repository.RoleRepository;
import site.clickbasketecom.ClickBasket.repository.UserRepository;
import site.clickbasketecom.ClickBasket.repository.VendorRepository;

/**
 * Service for vendor management operations.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Register a new vendor for an existing user.
     */
    @Transactional
    public VendorResponse registerVendor(Long userId, VendorRegisterRequest request) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if user is already a vendor
        if (vendorRepository.existsByUserId(userId)) {
            throw new EmailAlreadyExistsException("User is already registered as a vendor");
        }

        // Check if store name already exists
        if (vendorRepository.existsByStoreName(request.getStoreName())) {
            throw new EmailAlreadyExistsException("Store name is already taken: " + request.getStoreName());
        }

        // Add VENDOR role to user if not present
        Role vendorRole = roleRepository.findByName("ROLE_VENDOR")
                .orElseThrow(() -> new RoleNotFoundException("Vendor role not found"));
        user.getRoles().add(vendorRole);
        userRepository.save(user);

        // Create vendor
        Vendor vendor = Vendor.builder()
                .user(user)
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .storeLogoUrl(request.getStoreLogoUrl())
                .storeBannerUrl(request.getStoreBannerUrl())
                .businessEmail(request.getBusinessEmail())
                .businessPhone(request.getBusinessPhone())
                .businessAddress(request.getBusinessAddress())
                .taxId(request.getTaxId())
                .status(Vendor.VendorStatus.PENDING)
                .verified(false)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        return mapToResponse(savedVendor);
    }

    /**
     * Get vendor by ID.
     */
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));
        return mapToResponse(vendor);
    }

    /**
     * Get vendor by user ID.
     */
    @Transactional(readOnly = true)
    public VendorResponse getVendorByUserId(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new VendorNotFoundException("Vendor not found for user ID: " + userId));
        return mapToResponse(vendor);
    }

    /**
     * Get all vendors with pagination.
     */
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get vendors by status with pagination.
     */
    @Transactional(readOnly = true)
    public Page<VendorResponse> getVendorsByStatus(Vendor.VendorStatus status, Pageable pageable) {
        return vendorRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Update vendor profile.
     */
    @Transactional
    public VendorResponse updateVendor(Long vendorId, VendorUpdateRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        // Update only non-null fields
        if (request.getStoreName() != null) {
            // Check if new store name is already taken by another vendor
            if (!vendor.getStoreName().equals(request.getStoreName())
                    && vendorRepository.existsByStoreName(request.getStoreName())) {
                throw new EmailAlreadyExistsException("Store name is already taken: " + request.getStoreName());
            }
            vendor.setStoreName(request.getStoreName());
        }
        if (request.getStoreDescription() != null) {
            vendor.setStoreDescription(request.getStoreDescription());
        }
        if (request.getStoreLogoUrl() != null) {
            vendor.setStoreLogoUrl(request.getStoreLogoUrl());
        }
        if (request.getStoreBannerUrl() != null) {
            vendor.setStoreBannerUrl(request.getStoreBannerUrl());
        }
        if (request.getBusinessEmail() != null) {
            vendor.setBusinessEmail(request.getBusinessEmail());
        }
        if (request.getBusinessPhone() != null) {
            vendor.setBusinessPhone(request.getBusinessPhone());
        }
        if (request.getBusinessAddress() != null) {
            vendor.setBusinessAddress(request.getBusinessAddress());
        }
        if (request.getTaxId() != null) {
            vendor.setTaxId(request.getTaxId());
        }

        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToResponse(updatedVendor);
    }

    /**
     * Update vendor profile by user ID.
     */
    @Transactional
    public VendorResponse updateVendorByUserId(Long userId, VendorUpdateRequest request) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new VendorNotFoundException("Vendor not found for user ID: " + userId));
        return updateVendor(vendor.getId(), request);
    }

    /**
     * Approve a vendor (admin only).
     */
    @Transactional
    public VendorResponse approveVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        vendor.setStatus(Vendor.VendorStatus.APPROVED);
        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToResponse(updatedVendor);
    }

    /**
     * Reject a vendor (admin only).
     */
    @Transactional
    public VendorResponse rejectVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        vendor.setStatus(Vendor.VendorStatus.REJECTED);
        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToResponse(updatedVendor);
    }

    /**
     * Suspend a vendor (admin only).
     */
    @Transactional
    public VendorResponse suspendVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        vendor.setStatus(Vendor.VendorStatus.SUSPENDED);
        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToResponse(updatedVendor);
    }

    /**
     * Verify if vendor is approved.
     * Throws VendorNotApprovedException if not approved.
     */
    @Transactional(readOnly = true)
    public void ensureVendorApproved(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        if (vendor.getStatus() != Vendor.VendorStatus.APPROVED) {
            throw new VendorNotApprovedException();
        }
    }

    /**
     * Verify if vendor is approved by user ID.
     * Throws VendorNotApprovedException if not approved.
     */
    @Transactional(readOnly = true)
    public Vendor ensureVendorApprovedByUserId(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new VendorNotFoundException("Vendor not found for user ID: " + userId));

        if (vendor.getStatus() != Vendor.VendorStatus.APPROVED) {
            throw new VendorNotApprovedException();
        }
        return vendor;
    }

    /**
     * Check if vendor is approved (returns boolean instead of throwing).
     */
    @Transactional(readOnly = true)
    public boolean isVendorApproved(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(vendor -> vendor.getStatus() == Vendor.VendorStatus.APPROVED)
                .orElse(false);
    }

    /**
     * Map Vendor entity to VendorResponse DTO.
     */
    private VendorResponse mapToResponse(Vendor vendor) {
        User user = vendor.getUser();
        return VendorResponse.builder()
                .id(vendor.getId())
                .userId(user.getId())
                .ownerName(user.getFullName())
                .ownerEmail(user.getEmail())
                .storeName(vendor.getStoreName())
                .storeDescription(vendor.getStoreDescription())
                .storeLogoUrl(vendor.getStoreLogoUrl())
                .storeBannerUrl(vendor.getStoreBannerUrl())
                .businessEmail(vendor.getBusinessEmail())
                .businessPhone(vendor.getBusinessPhone())
                .businessAddress(vendor.getBusinessAddress())
                .taxId(vendor.getTaxId())
                .commissionRate(vendor.getCommissionRate())
                .status(vendor.getStatus().name())
                .verified(vendor.getVerified())
                .rating(vendor.getRating())
                .totalReviews(vendor.getTotalReviews())
                .totalProducts(vendor.getProducts() != null ? vendor.getProducts().size() : 0)
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }
}
