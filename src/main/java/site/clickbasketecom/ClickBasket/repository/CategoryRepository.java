package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all root categories (categories without parent).
     */
    List<Category> findByParentIsNull();

    /**
     * Find all subcategories of a parent category.
     */
    List<Category> findByParentId(Long parentId);

    /**
     * Find category by name.
     */
    Optional<Category> findByName(String name);

    /**
     * Check if category name exists.
     */
    Boolean existsByName(String name);

    /**
     * Find all active categories.
     */
    List<Category> findByIsActiveTrue();

    /**
     * Find all active root categories.
     */
    List<Category> findByParentIsNullAndIsActiveTrue();

    /**
     * Find all active subcategories of a parent.
     */
    List<Category> findByParentIdAndIsActiveTrue(Long parentId);

    /**
     * Count subcategories of a parent.
     */
    Long countByParentId(Long parentId);
}
