package site.clickbasketecom.ClickBasket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.clickbasketecom.ClickBasket.dto.category.CategoryResponse;
import site.clickbasketecom.ClickBasket.dto.category.CreateCategoryRequest;
import site.clickbasketecom.ClickBasket.dto.category.UpdateCategoryRequest;
import site.clickbasketecom.ClickBasket.entity.Category;
import site.clickbasketecom.ClickBasket.exception.CategoryNotFoundException;
import site.clickbasketecom.ClickBasket.exception.EmailAlreadyExistsException;
import site.clickbasketecom.ClickBasket.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for category management operations.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Create a new category.
     */
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Check if name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new EmailAlreadyExistsException("Category name already exists: " + request.getName());
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentId()));
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .parent(parent)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(true)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory, false);
    }

    /**
     * Get category by ID.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return mapToResponse(category, true);
    }

    /**
     * Get all root categories (for admin).
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(cat -> mapToResponse(cat, true))
                .collect(Collectors.toList());
    }

    /**
     * Get all active root categories (for users).
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveRootCategories() {
        return categoryRepository.findByParentIsNullAndIsActiveTrue().stream()
                .map(cat -> mapToResponse(cat, true))
                .collect(Collectors.toList());
    }

    /**
     * Get subcategories of a parent category.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(Long parentId) {
        // Verify parent exists
        if (!categoryRepository.existsById(parentId)) {
            throw new CategoryNotFoundException(parentId);
        }
        return categoryRepository.findByParentIdAndIsActiveTrue(parentId).stream()
                .map(cat -> mapToResponse(cat, false))
                .collect(Collectors.toList());
    }

    /**
     * Get all categories as flat list (for admin).
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> mapToResponse(cat, false))
                .collect(Collectors.toList());
    }

    /**
     * Update a category.
     */
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // Update name if provided and different
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new EmailAlreadyExistsException("Category name already exists: " + request.getName());
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }

        // Update parent - allow setting to null (make root category)
        if (request.getParentId() != null) {
            // Prevent setting itself as parent
            if (request.getParentId().equals(categoryId)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentId()));
            category.setParent(parent);
        }

        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }

        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory, false);
    }

    /**
     * Delete a category (soft delete by setting inactive).
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // Check if category has subcategories
        Long subcategoryCount = categoryRepository.countByParentId(categoryId);
        if (subcategoryCount > 0) {
            throw new IllegalStateException("Cannot delete category with subcategories. Remove subcategories first.");
        }

        // Soft delete - set inactive
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    /**
     * Permanently delete a category (admin only).
     */
    @Transactional
    public void permanentlyDeleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        // Check if category has subcategories
        Long subcategoryCount = categoryRepository.countByParentId(categoryId);
        if (subcategoryCount > 0) {
            throw new IllegalStateException("Cannot delete category with subcategories. Remove subcategories first.");
        }

        categoryRepository.deleteById(categoryId);
    }

    /**
     * Map Category entity to CategoryResponse DTO.
     */
    private CategoryResponse mapToResponse(Category category, boolean includeSubcategories) {
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());

        // Add parent info if exists
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                    .parentName(category.getParent().getName());
        }

        // Add subcategory count
        if (category.getChildren() != null) {
            builder.subcategoryCount(category.getChildren().size());
        }

        // Add product count
        if (category.getProducts() != null) {
            builder.productCount(category.getProducts().size());
        }

        // Include active subcategories if requested
        if (includeSubcategories && category.getChildren() != null) {
            List<CategoryResponse> subcategories = category.getChildren().stream()
                    .filter(Category::getIsActive)
                    .map(sub -> mapToResponse(sub, false))
                    .collect(Collectors.toList());
            builder.subcategories(subcategories);
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
