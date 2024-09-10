CREATE TABLE IF NOT EXISTS shopping_cart (
    shopping_cart_id VARCHAR(36) NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    customer_id VARCHAR(40),
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS item (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    quantity INT NOT NULL,
    product_id VARCHAR(40),
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    shopping_cart_id VARCHAR(36) REFERENCES shopping_cart(shopping_cart_id),
    INDEX item_shopping_cart_id_idx(shopping_cart_id)
);