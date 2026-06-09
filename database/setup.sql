-- eMart Invoice Generator Database Setup
-- Idempotent MySQL setup and migration script.
--
-- Safe to run repeatedly. It creates the database/schema, brings older local
-- schemas forward, migrates invoice_log to invoices only when needed, clears
-- existing application data, and inserts a fresh demo dataset.

CREATE DATABASE IF NOT EXISTS emart_invoices;
USE emart_invoices;

SET @invoices_table_existed_before_setup := (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'invoices'
);

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin', 'manager') DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    current_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATETIME NOT NULL,
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NULL
);

CREATE TABLE IF NOT EXISTS invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pdf_path VARCHAR(500),
    email_sent BOOLEAN DEFAULT FALSE,
    email_sent_date TIMESTAMP NULL,
    generated_by_user_id INT
);

DELIMITER $$

DROP PROCEDURE IF EXISTS add_column_if_missing $$
CREATE PROCEDURE add_column_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = p_table_name
          AND column_name = p_column_name
    ) THEN
        SET @sql := CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD COLUMN `',
            p_column_name, '` ', p_column_definition
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS modify_column_if_different $$
CREATE PROCEDURE modify_column_if_different(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_definition TEXT,
    IN p_expected_column_type VARCHAR(255),
    IN p_expected_is_nullable VARCHAR(3),
    IN p_expected_column_default TEXT
)
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = p_table_name
          AND column_name = p_column_name
          AND (
              column_type <> p_expected_column_type
              OR is_nullable <> p_expected_is_nullable
              OR NOT (column_default <=> p_expected_column_default)
          )
    ) THEN
        SET @sql := CONCAT(
            'ALTER TABLE `', p_table_name, '` MODIFY COLUMN `',
            p_column_name, '` ', p_column_definition
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS drop_column_if_exists $$
CREATE PROCEDURE drop_column_if_exists(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64)
)
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = p_table_name
          AND column_name = p_column_name
    ) THEN
        SET @sql := CONCAT('ALTER TABLE `', p_table_name, '` DROP COLUMN `', p_column_name, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS add_index_if_missing $$
CREATE PROCEDURE add_index_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_index_name VARCHAR(64),
    IN p_index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = p_table_name
          AND index_name = p_index_name
    ) THEN
        SET @sql := CONCAT('CREATE INDEX `', p_index_name, '` ON `', p_table_name, '` ', p_index_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS drop_fk_if_exists $$
CREATE PROCEDURE drop_fk_if_exists(
    IN p_table_name VARCHAR(64),
    IN p_constraint_name VARCHAR(64)
)
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = DATABASE()
          AND table_name = p_table_name
          AND constraint_name = p_constraint_name
          AND constraint_type = 'FOREIGN KEY'
    ) THEN
        SET @sql := CONCAT('ALTER TABLE `', p_table_name, '` DROP FOREIGN KEY `', p_constraint_name, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS add_fk_if_missing $$
CREATE PROCEDURE add_fk_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_constraint_name VARCHAR(64),
    IN p_constraint_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = DATABASE()
          AND table_name = p_table_name
          AND constraint_name = p_constraint_name
          AND constraint_type = 'FOREIGN KEY'
    ) THEN
        SET @sql := CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD CONSTRAINT `',
            p_constraint_name, '` ', p_constraint_definition
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DROP PROCEDURE IF EXISTS migrate_invoice_log_if_needed $$
CREATE PROCEDURE migrate_invoice_log_if_needed()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = DATABASE()
          AND table_name = 'invoice_log'
    ) AND (
        @invoices_table_existed_before_setup = 0
        OR NOT EXISTS (SELECT 1 FROM invoices LIMIT 1)
    ) THEN
        SET @has_admin_user := (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name = 'invoice_log'
              AND column_name = 'admin_user'
        );

        IF @has_admin_user > 0 THEN
            INSERT INTO invoices (
                id,
                order_id,
                generated_date,
                pdf_path,
                email_sent,
                email_sent_date,
                generated_by_user_id
            )
            SELECT
                il.id,
                il.order_id,
                COALESCE(il.generated_date, CURRENT_TIMESTAMP),
                il.pdf_path,
                COALESCE(il.email_sent, FALSE),
                il.email_sent_date,
                u.id
            FROM invoice_log il
            LEFT JOIN users u ON u.username = il.admin_user
            WHERE NOT EXISTS (
                SELECT 1
                FROM invoices i
                WHERE i.id = il.id
            );
        ELSE
            INSERT INTO invoices (
                id,
                order_id,
                generated_date,
                pdf_path,
                email_sent,
                email_sent_date
            )
            SELECT
                il.id,
                il.order_id,
                COALESCE(il.generated_date, CURRENT_TIMESTAMP),
                il.pdf_path,
                COALESCE(il.email_sent, FALSE),
                il.email_sent_date
            FROM invoice_log il
            WHERE NOT EXISTS (
                SELECT 1
                FROM invoices i
                WHERE i.id = il.id
            );
        END IF;

        DROP TABLE invoice_log;
    END IF;
END $$

DROP PROCEDURE IF EXISTS migrate_legacy_order_items_if_needed $$
CREATE PROCEDURE migrate_legacy_order_items_if_needed()
BEGIN
    SET @has_order_item_product_name := (
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'order_items'
          AND column_name = 'product_name'
    );

    SET @has_order_item_price := (
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'order_items'
          AND column_name = 'price'
    );

    IF @has_order_item_product_name > 0 AND @has_order_item_price > 0 THEN
        INSERT INTO products (name, current_price)
        SELECT oi.product_name, MAX(COALESCE(oi.price, 0.00))
        FROM order_items oi
        WHERE oi.product_name IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM products p
              WHERE p.name = oi.product_name
          )
        GROUP BY oi.product_name;

        UPDATE order_items oi
        JOIN products p ON p.name = oi.product_name
        SET oi.product_id = p.id
        WHERE oi.product_id IS NULL;

        UPDATE order_items
        SET unit_price = price
        WHERE unit_price IS NULL;
    ELSEIF @has_order_item_product_name > 0 THEN
        INSERT INTO products (name, current_price)
        SELECT oi.product_name, 0.00
        FROM order_items oi
        WHERE oi.product_name IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM products p
              WHERE p.name = oi.product_name
          )
        GROUP BY oi.product_name;

        UPDATE order_items oi
        JOIN products p ON p.name = oi.product_name
        SET oi.product_id = p.id
        WHERE oi.product_id IS NULL;
    ELSEIF @has_order_item_price > 0 THEN
        UPDATE order_items
        SET unit_price = price
        WHERE unit_price IS NULL;
    END IF;
END $$

DELIMITER ;

CALL add_column_if_missing('customers', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL modify_column_if_different('customers', 'name', 'VARCHAR(100) NOT NULL', 'varchar(100)', 'NO', NULL);
CALL modify_column_if_different('customers', 'email', 'VARCHAR(100) NOT NULL', 'varchar(100)', 'NO', NULL);
UPDATE customers SET address = '' WHERE address IS NULL;
CALL modify_column_if_different('customers', 'address', 'TEXT NOT NULL', 'text', 'NO', NULL);

CALL modify_column_if_different('users', 'username', 'VARCHAR(50) NOT NULL', 'varchar(50)', 'NO', NULL);
CALL modify_column_if_different('users', 'password_hash', 'VARCHAR(255) NOT NULL', 'varchar(255)', 'NO', NULL);
CALL modify_column_if_different('users', 'role', 'ENUM(''admin'', ''manager'') DEFAULT ''admin''', 'enum(''admin'',''manager'')', 'YES', 'admin');

CALL add_column_if_missing('products', 'current_price', 'DECIMAL(10,2) NULL');
CALL add_column_if_missing('products', 'created_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP');

UPDATE products
SET current_price = 0.00
WHERE current_price IS NULL;

CALL modify_column_if_different('products', 'name', 'VARCHAR(200) NOT NULL', 'varchar(200)', 'NO', NULL);
CALL modify_column_if_different('products', 'current_price', 'DECIMAL(10,2) NOT NULL', 'decimal(10,2)', 'NO', NULL);

CALL add_column_if_missing('orders', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL modify_column_if_different('orders', 'status', 'ENUM(''pending'', ''completed'', ''cancelled'') DEFAULT ''pending''', 'enum(''pending'',''completed'',''cancelled'')', 'YES', 'pending');

CALL add_column_if_missing('order_items', 'product_id', 'INT NULL');
CALL add_column_if_missing('order_items', 'unit_price', 'DECIMAL(10,2) NULL');
CALL migrate_legacy_order_items_if_needed();

UPDATE order_items oi
JOIN products p ON p.name = CONCAT('Migrated Product ', oi.id)
SET oi.product_id = p.id
WHERE oi.product_id IS NULL;

INSERT INTO products (name, current_price)
SELECT CONCAT('Migrated Product ', oi.id), COALESCE(oi.unit_price, 0.00)
FROM order_items oi
WHERE oi.product_id IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM products p
      WHERE p.name = CONCAT('Migrated Product ', oi.id)
  );

UPDATE order_items oi
JOIN products p ON p.name = CONCAT('Migrated Product ', oi.id)
SET oi.product_id = p.id
WHERE oi.product_id IS NULL;

UPDATE order_items
SET unit_price = 0.00
WHERE unit_price IS NULL;

CALL drop_fk_if_exists('order_items', 'fk_order_items_product');
CALL drop_fk_if_exists('order_items', 'fk_order_items_order');
CALL drop_fk_if_exists('order_items', 'order_items_ibfk_1');
CALL drop_fk_if_exists('order_items', 'order_items_ibfk_2');
CALL drop_fk_if_exists('orders', 'fk_orders_customer');
CALL drop_fk_if_exists('orders', 'orders_ibfk_1');
CALL drop_fk_if_exists('invoices', 'fk_invoices_order');
CALL drop_fk_if_exists('invoices', 'fk_invoices_generated_by_user');
CALL drop_fk_if_exists('invoices', 'invoices_ibfk_1');
CALL drop_fk_if_exists('invoices', 'invoices_ibfk_2');

CALL modify_column_if_different('order_items', 'product_id', 'INT NOT NULL', 'int', 'NO', NULL);
CALL modify_column_if_different('order_items', 'unit_price', 'DECIMAL(10,2) NOT NULL', 'decimal(10,2)', 'NO', NULL);

CALL drop_column_if_exists('order_items', 'product_name');
CALL drop_column_if_exists('order_items', 'price');
CALL drop_column_if_exists('order_items', 'subtotal');
CALL drop_column_if_exists('products', 'description');
CALL drop_column_if_exists('products', 'stock_quantity');
CALL drop_column_if_exists('products', 'updated_at');

CALL add_index_if_missing('orders', 'idx_orders_customer_id', '(customer_id)');
CALL add_index_if_missing('orders', 'idx_orders_order_date', '(order_date)');
CALL add_index_if_missing('order_items', 'idx_order_items_order_id', '(order_id)');
CALL add_index_if_missing('order_items', 'idx_order_items_product_id', '(product_id)');
CALL add_index_if_missing('invoices', 'idx_invoices_order_id', '(order_id)');
CALL add_index_if_missing('invoices', 'idx_invoices_generated_date', '(generated_date)');
CALL add_index_if_missing('invoices', 'idx_invoices_generated_by_user_id', '(generated_by_user_id)');

CALL add_fk_if_missing('orders', 'fk_orders_customer',
    'FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT');
CALL add_fk_if_missing('order_items', 'fk_order_items_order',
    'FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE');
CALL add_fk_if_missing('order_items', 'fk_order_items_product',
    'FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT');
CALL add_fk_if_missing('invoices', 'fk_invoices_order',
    'FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE');
CALL add_fk_if_missing('invoices', 'fk_invoices_generated_by_user',
    'FOREIGN KEY (generated_by_user_id) REFERENCES users(id) ON DELETE SET NULL');

CALL migrate_invoice_log_if_needed();

DELETE FROM invoices;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM products;
DELETE FROM customers;
DELETE FROM users;

ALTER TABLE invoices AUTO_INCREMENT = 1;
ALTER TABLE order_items AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE products AUTO_INCREMENT = 1;
ALTER TABLE customers AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

INSERT INTO customers (id, name, email, phone, address) VALUES
(1, 'Aarav Mehta', 'aarav.mehta@example.com', '+91-98765-41001', '12 Market Road, Pune, Maharashtra 411001'),
(2, 'Nisha Kapoor', 'nisha.kapoor@example.com', '+91-98765-41002', '48 Lake View Street, Bengaluru, Karnataka 560001'),
(3, 'Rohan Iyer', 'rohan.iyer@example.com', '+91-98765-41003', '7 Park Avenue, Chennai, Tamil Nadu 600001'),
(4, 'Priya Sharma', 'priya.sharma@example.com', '+91-98765-41004', '22 Civil Lines, Jaipur, Rajasthan 302001')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    phone = VALUES(phone),
    address = VALUES(address);

INSERT INTO users (id, username, password_hash, role) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1VdLSnqpjx.ocs.hpvZu.1yNjHdyJrq', 'admin')
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    role = VALUES(role);

INSERT INTO products (id, name, current_price) VALUES
(1, 'Thermal Receipt Printer', 8499.00),
(2, 'Barcode Scanner', 3299.00),
(3, 'POS Cash Drawer', 5499.00),
(4, 'Billing Paper Roll Pack', 699.00),
(5, 'Wireless Keyboard Mouse Combo', 1499.00),
(6, 'Inventory Label Stickers', 399.00)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    current_price = VALUES(current_price);

INSERT INTO orders (id, customer_id, order_date, status, total_amount) VALUES
(1, 1, '2026-06-01 10:30:00', 'completed', 11798.00),
(2, 2, '2026-06-03 14:15:00', 'completed', 6997.00),
(3, 3, '2026-06-05 09:45:00', 'pending', 2597.00),
(4, 4, '2026-06-07 16:20:00', 'completed', 12397.00)
ON DUPLICATE KEY UPDATE
    customer_id = VALUES(customer_id),
    order_date = VALUES(order_date),
    status = VALUES(status),
    total_amount = VALUES(total_amount);

INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 1, 8499.00),
(2, 1, 2, 1, 3299.00),
(3, 2, 3, 1, 5499.00),
(4, 2, 5, 1, 1499.00),
(5, 3, 4, 2, 699.00),
(6, 3, 6, 3, 399.00),
(7, 4, 1, 1, 8499.00),
(8, 4, 4, 2, 699.00),
(9, 4, 5, 1, 1499.00),
(10, 4, 6, 2, 399.00)
ON DUPLICATE KEY UPDATE
    order_id = VALUES(order_id),
    product_id = VALUES(product_id),
    quantity = VALUES(quantity),
    unit_price = VALUES(unit_price);

INSERT INTO invoices (id, order_id, generated_date, pdf_path, email_sent, email_sent_date, generated_by_user_id) VALUES
(1, 1, '2026-06-01 10:35:00', 'invoices/invoice-1.pdf', TRUE, '2026-06-01 10:40:00', 1),
(2, 2, '2026-06-03 14:20:00', 'invoices/invoice-2.pdf', TRUE, '2026-06-03 14:24:00', 1),
(3, 4, '2026-06-07 16:25:00', 'invoices/invoice-4.pdf', FALSE, NULL, 1)
ON DUPLICATE KEY UPDATE
    order_id = VALUES(order_id),
    generated_date = VALUES(generated_date),
    pdf_path = VALUES(pdf_path),
    email_sent = VALUES(email_sent),
    email_sent_date = VALUES(email_sent_date),
    generated_by_user_id = VALUES(generated_by_user_id);

DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS modify_column_if_different;
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS add_index_if_missing;
DROP PROCEDURE IF EXISTS drop_fk_if_exists;
DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS migrate_invoice_log_if_needed;
DROP PROCEDURE IF EXISTS migrate_legacy_order_items_if_needed;

SHOW TABLES;
