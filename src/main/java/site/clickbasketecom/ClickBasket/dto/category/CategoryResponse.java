package site.clickbasketecom.ClickBasket.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for category response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentId;
    private String parentName;
    private Integer displayOrder;
    private Boolean isActive;
    private Integer subcategoryCount;
    private Integer productCount;
    private List<CategoryResponse> subcategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
