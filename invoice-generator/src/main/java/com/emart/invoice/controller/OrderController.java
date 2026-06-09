package com.emart.invoice.controller;

import com.emart.invoice.entity.Order;
import com.emart.invoice.entity.Customer;
import com.emart.invoice.entity.Product;
import com.emart.invoice.entity.OrderItem;
import com.emart.invoice.repository.OrderRepository;
import com.emart.invoice.repository.CustomerRepository;
import com.emart.invoice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Integer customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }
    
    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    // Create new order (simplified version)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderRequest) {
        try {
            System.out.println("=== CREATING ORDER ===");
            System.out.println("Request data: " + orderRequest);

            // Extract customer ID
            Integer customerId = (Integer) orderRequest.get("customerId");
            if (customerId == null) {
                System.err.println("Customer ID is null");
                return ResponseEntity.badRequest().body("Customer ID is required");
            }

            Optional<Customer> customerOpt = customerRepository.findById(customerId);

            if (!customerOpt.isPresent()) {
                System.err.println("Customer not found with ID: " + customerId);
                return ResponseEntity.badRequest().body("Customer not found with ID: " + customerId);
            }
            
            Customer customer = customerOpt.get();
            
            // Create new order
            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("pending");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            
            // Process order items
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");

            if (items == null || items.isEmpty()) {
                System.err.println("No items provided in order");
                return ResponseEntity.badRequest().body("Order must contain at least one item");
            }

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Map<String, Object> item : items) {
                System.out.println("Processing item: " + item);

                Integer productId = (Integer) item.get("productId");
                Integer quantity = (Integer) item.get("quantity");

                if (productId == null) {
                    System.err.println("Product ID is null for item: " + item);
                    return ResponseEntity.badRequest().body("Product ID is required for all items");
                }

                if (quantity == null || quantity <= 0) {
                    System.err.println("Invalid quantity for item: " + item);
                    return ResponseEntity.badRequest().body("Valid quantity is required for all items");
                }

                Optional<Product> productOpt = productRepository.findById(productId);
                if (!productOpt.isPresent()) {
                    System.err.println("Product not found with ID: " + productId);
                    return ResponseEntity.badRequest().body("Product not found with ID: " + productId);
                }

                Product product = productOpt.get();

                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setUnitPrice(product.getCurrentPrice());
                
                orderItems.add(orderItem);
                totalAmount = totalAmount.add(orderItem.getSubtotal());
            }
            
            order.setTotalAmount(totalAmount);
            order.setOrderItems(orderItems);

            // Save order
            System.out.println("Saving order with total amount: " + totalAmount);
            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully with ID: " + savedOrder.getId());

            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }
    
    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusUpdate) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(statusUpdate.get("status"));
            order.setUpdatedAt(LocalDateTime.now());
            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get recent orders (last 30 days)
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> recentOrders = orderRepository.findRecentOrders(thirtyDaysAgo);
        return ResponseEntity.ok(recentOrders);
    }
    
    // Get order count by status
    @GetMapping("/count/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(@PathVariable String status) {
        long count = orderRepository.countByStatus(status);
        return ResponseEntity.ok(count);
    }
}
