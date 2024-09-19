-- create.sql
-- Create the inventory table
CREATE TABLE IF NOT EXISTS inventory (
    id CHAR(36) PRIMARY KEY,
    version int NOT NULL,
    sku_code VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    status ENUM(
    'DISCONTINUED',
    'AVAILABLE',
    'RESERVED',
    'OUT_OF_STOCK'
) NOT NULL,
    product_id CHAR(36) UNIQUE NOT NULL 
);

-- Create the reserved_products table
CREATE TABLE IF NOT EXISTS reserved_item (
    id CHAR(36) PRIMARY KEY,
    version int NOT NULL,
    quantity INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    inventory_id CHAR(36),
    FOREIGN KEY (inventory_id) REFERENCES inventory(id)
);

-- Optionally, create an index on the inventory_id column in reserved_products for better performance
CREATE INDEX idx_inventory_id ON reserved_item (inventory_id);
