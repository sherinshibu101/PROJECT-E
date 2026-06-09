package com.emart.invoice.repository;

import com.emart.invoice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Find product by name (useful for searching)
    Optional<Product> findByName(String name);
    
    List<Product> findByCurrentPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find products by name containing text (for search functionality)
    List<Product> findByNameContainingIgnoreCase(String searchTerm);
    
    // Check if product exists by name (useful for validation)
    boolean existsByName(String name);
}
