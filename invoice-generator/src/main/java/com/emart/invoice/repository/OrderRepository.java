package com.emart.invoice.repository;

import com.emart.invoice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Find orders by customer ID
    List<Order> findByCustomerId(Integer customerId);
    
    // Find orders by status
    List<Order> findByStatus(String status);
    
    // Find orders by date range
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recent orders (last 30 days)
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :thirtyDaysAgo ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    // Find orders by customer email (useful for search)
    @Query("SELECT o FROM Order o WHERE o.customer.email = :email")
    List<Order> findByCustomerEmail(@Param("email") String email);
    
    // Count orders by status
    long countByStatus(String status);
}