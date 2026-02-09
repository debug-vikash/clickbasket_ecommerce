package site.clickbasketecom.ClickBasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.clickbasketecom.ClickBasket.entity.Vendor;

import java.util.Optional;

/**
 * Repository for Vendor entity.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByUserId(Long userId);

    Optional<Vendor> findByStoreName(String storeName);

    Boolean existsByUserId(Long userId);

    Boolean existsByStoreName(String storeName);

    Page<Vendor> findByStatus(Vendor.VendorStatus status, Pageable pageable);
}
