-- PostgreSQL Database Setup for EMART Invoice System
-- Run this script in your PostgreSQL database

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    order_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order Items table
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES orders(id),
    product_id INTEGER REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL
);

-- Insert default admin user (password: admin123)
-- Only insert if not exists
INSERT INTO users (username, password, email) 
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyifkPiuGHG', 'admin@emart.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Insert sample data for testing (optional)
-- Sample customers
INSERT INTO customers (name, email, phone, address) 
SELECT 'John Doe', 'john@example.com', '+1234567890', '123 Main St, City, State'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'john@example.com');

INSERT INTO customers (name, email, phone, address) 
SELECT 'Jane Smith', 'jane@example.com', '+1987654321', '456 Oak Ave, City, State'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'jane@example.com');

-- Sample products
INSERT INTO products (name, description, price, stock_quantity) 
SELECT 'Laptop', 'High-performance laptop', 999.99, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Laptop');

INSERT INTO products (name, description, price, stock_quantity) 
SELECT 'Mouse', 'Wireless optical mouse', 29.99, 50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mouse');

INSERT INTO products (name, description, price, stock_quantity) 
SELECT 'Keyboard', 'Mechanical keyboard', 79.99, 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Keyboard');

-- Verify tables created successfully
SELECT 'Tables created successfully!' as status;
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
