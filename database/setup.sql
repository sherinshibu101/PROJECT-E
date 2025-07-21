-- eMart Invoice Generator Database Setup
-- Created: 2025-07-12
-- Purpose: Create database and tables for invoice management system

-- Create database
CREATE DATABASE IF NOT EXISTS emart_invoices;
USE emart_invoices;

-- Table 1: Customers
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Orders
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Table 3: Order Items
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Table 4: Invoice Log
CREATE TABLE invoice_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_sent BOOLEAN DEFAULT FALSE,
    admin_user VARCHAR(50),
    pdf_path VARCHAR(500),
    email_sent_date TIMESTAMP NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Table 5: Admin Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin', 'manager') DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Create indexes for better performance
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_date ON orders(order_date);
CREATE INDEX idx_invoice_order ON invoice_log(order_id);
CREATE INDEX idx_invoice_date ON invoice_log(generated_date);

-- Insert sample data for testing
INSERT INTO customers (name, email, address, phone) VALUES
('John Doe', 'john.doe@email.com', '123 Main St, City, State 12345', '+1-555-0123'),
('Jane Smith', 'jane.smith@email.com', '456 Oak Ave, City, State 12346', '+1-555-0124'),
('Bob Johnson', 'bob.johnson@email.com', '789 Pine Rd, City, State 12347', '+1-555-0125');

INSERT INTO orders (customer_id, order_date, total_amount, status) VALUES
(1, '2025-07-10 10:30:00', 299.99, 'completed'),
(2, '2025-07-11 14:15:00', 149.50, 'completed'),
(3, '2025-07-12 09:45:00', 89.99, 'pending');

INSERT INTO order_items (order_id, product_name, quantity, price, subtotal) VALUES
(1, 'Wireless Headphones', 1, 199.99, 199.99),
(1, 'Phone Case', 2, 50.00, 100.00),
(2, 'Bluetooth Speaker', 1, 149.50, 149.50),
(3, 'USB Cable', 3, 29.99, 89.97);

-- Insert default admin user (password: admin123)
-- Note: In production, use proper password hashing
INSERT INTO users (username, password_hash, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1VdLSnqpjx.ocs.hpvZu.1yNjHdyJrq', 'admin');

-- Show created tables
SHOW TABLES;

-- Display sample data
SELECT 'Customers:' as Info;
SELECT * FROM customers;

SELECT 'Orders:' as Info;
SELECT * FROM orders;

SELECT 'Order Items:' as Info;
SELECT * FROM order_items;

SELECT 'Users:' as Info;
SELECT id, username, role, created_at FROM users;
