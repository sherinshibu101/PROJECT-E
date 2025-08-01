package com.emart.invoice.repository;

import com.emart.invoice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Find product by name (useful for searching)
    Optional<Product> findByName(String name);
    
    // Find products by price range (useful for filtering)
    List<Product> findByCurrentPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products with stock above certain quantity
    List<Product> findByStockQuantityGreaterThan(Integer minStock);
    
    // Find products with low stock (simplified method name)
    List<Product> findByStockQuantityLessThanEqual(Integer threshold);
    
    // Find products by name containing text (for search functionality)
    List<Product> findByNameContainingIgnoreCase(String searchTerm);
    
    // Check if product exists by name (useful for validation)
    boolean existsByName(String name);

    // Reset AUTO_INCREMENT to start from 1
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE products AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}