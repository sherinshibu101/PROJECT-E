package com.emart.invoice.repository;

import com.emart.invoice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Find all items for a specific order
    List<OrderItem> findByOrderId(Integer orderId);
    
    List<OrderItem> findByProductId(Integer productId);
}
