package site.clickbasketecom.ClickBasket.dto.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    /**
     * Parent category ID. Set to null to make root category.
     */
    private Long parentId;

    /**
     * Display order. Lower values appear first.
     */
    private Integer displayOrder;

    /**
     * Whether the category is active.
     */
    private Boolean isActive;
}
