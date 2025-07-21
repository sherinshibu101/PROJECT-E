package com.emart.invoice.controller;

import com.emart.invoice.entity.Customer;
import com.emart.invoice.entity.Order;
import com.emart.invoice.entity.OrderItem;
import com.emart.invoice.repository.CustomerRepository;
import com.emart.invoice.repository.OrderRepository;
import com.emart.invoice.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class DatabaseTestController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @GetMapping("/test/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    @GetMapping("/test/customer/{id}")
    public Customer getCustomer(@PathVariable Integer id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    @GetMapping("/test/orders")
    public String getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return "Found " + orders.size() + " orders: " + orders.toString();
        } catch (Exception e) {
            return "Error getting orders: " + e.getMessage();
        }
    }
    
    @GetMapping("/test/order/{id}/items")
    public List<OrderItem> getOrderItems(@PathVariable Integer id) {
        return orderItemRepository.findByOrderId(id);
    }
    
    @GetMapping("/test/invoice-data/{orderId}")
    public String getInvoiceData(@PathVariable Integer orderId) {
        // This simulates what we'll need for invoice generation
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "Order not found!";
        }
        
        Customer customer = order.getCustomer();
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        
        StringBuilder result = new StringBuilder();
        result.append("=== INVOICE DATA ===\n");
        result.append("Customer: ").append(customer.getName()).append("\n");
        result.append("Email: ").append(customer.getEmail()).append("\n");
        result.append("Address: ").append(customer.getAddress()).append("\n");
        result.append("Order Date: ").append(order.getOrderDate()).append("\n");
        result.append("Status: ").append(order.getStatus()).append("\n");
        result.append("Items:\n");
        
        for (OrderItem item : items) {
            result.append("- ").append(item.getProductName())
                  .append(" × ").append(item.getQuantity())
                  .append(" @ $").append(item.getPrice())
                  .append(" = $").append(item.getSubtotal()).append("\n");
        }
        
        result.append("Total: $").append(order.getTotalAmount());
        
        return result.toString();
    }
}