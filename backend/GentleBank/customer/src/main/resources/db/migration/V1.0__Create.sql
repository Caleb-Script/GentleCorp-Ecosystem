CREATE TABLE IF NOT EXISTS address
(
    id           BINARY(16)   NOT NULL PRIMARY KEY,
    street       VARCHAR(100) NOT NULL,
    house_number VARCHAR(5)   NOT NULL,
    zip_code     VARCHAR(6)   NOT NULL,
    state        VARCHAR(20)  NOT NULL,
    city         VARCHAR(50)  NOT NULL,

    INDEX     address_zip_code_idx(zip_code)
);

CREATE TABLE IF NOT EXISTS customer
(
    id                   BINARY(16)   NOT NULL PRIMARY KEY,
    version              INTEGER      NOT NULL DEFAULT 0,
    last_name            VARCHAR(40)  NOT NULL,
    first_name           VARCHAR(40)  NOT NULL,
    email                VARCHAR(40)  NOT NULL UNIQUE,
    birth_date           date         NOT NULL,
    contact_options      VARCHAR(255) NOT NULL,
    gender               VARCHAR(7)   NOT NULL CHECK ( gender = 'MALE' OR gender = 'FEMALE' OR gender = 'DIVERSE' ),
    marital_status       VARCHAR(12)  NOT NULL,
    address_id           BINARY(16)   UNIQUE REFERENCES address,
    created              DATETIME NOT NULL,
    updated              DATETIME NOT NULL,
    username             VARCHAR(40)  NOT NULL UNIQUE,

    INDEX     customer_last_name_idx(last_name)
);