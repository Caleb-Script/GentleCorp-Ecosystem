
CREATE TABLE account (
    id               BINARY(16)     NOT NULL PRIMARY KEY,
    version          VARCHAR(20)    NOT NULL DEFAULT 0,
    balance          DECIMAL(10, 2) NOT NULL,
    rate_of_interest DECIMAL(10, 2) NOT NULL,
    category VARCHAR(20)    NOT NULL,
    state    VARCHAR(12)    NOT NULL,
    overdraft        INT,
    withdrawal_limit INT,
    created          DATETIME       NOT NULL,
    updated          DATETIME       NOT NULL,
    customer_id      BINARY(16)     NOT NULL,

    INDEX account_customer_id_idx(customer_id)
);
