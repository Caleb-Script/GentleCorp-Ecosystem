CREATE TABLE IF NOT EXISTS address
(
    id           BINARY(16)   NOT NULL PRIMARY KEY,
    street       VARCHAR(100) NOT NULL,
    house_number VARCHAR(5)   NOT NULL,
    zip_code     VARCHAR(6)   NOT NULL,
    city         VARCHAR(50) NOT NULL,
    state        VARCHAR(20)  NOT NULL,
    country      VARCHAR(20) NOT NULL,
    

    INDEX     address_zip_code_idx(zip_code)
);

CREATE TABLE IF NOT EXISTS customer
(
    id                   BINARY(16)   NOT NULL PRIMARY KEY,
    version              INTEGER      NOT NULL DEFAULT 0,
    last_name            VARCHAR(40)  NOT NULL,
    first_name           VARCHAR(40)  NOT NULL,
    email                VARCHAR(40)  NOT NULL UNIQUE,
    phone_number         VARCHAR(20),
    tier_level           INTEGER      NOT NULL DEFAULT 1,
    is_Subscribed        BIT(1)       NOT NULL DEFAULT FALSE,
    birth_date           date         NOT NULL,
    gender               VARCHAR(7)   NOT NULL CHECK ( gender = 'MALE' OR gender = 'FEMALE' OR gender = 'DIVERSE' ),
    marital_status       VARCHAR(12)  NOT NULL,
    customer_state      VARCHAR(12)  NOT NULL,
    address_id           BINARY(16)   UNIQUE REFERENCES address,
    created              DATETIME     NOT NULL,
    updated              DATETIME     NOT NULL,
    username             VARCHAR(40)  NOT NULL UNIQUE,
    contact_options      VARCHAR(255) NOT NULL,
    interests            VARCHAR(20),

    INDEX     customer_last_name_idx(last_name)
);

CREATE TABLE contact (
    id BINARY(16) NOT NULL PRIMARY KEY,
    version             VARCHAR(20) NOT NULL DEFAULT 0,
    last_name            VARCHAR(40) NOT NULL,
    first_name           VARCHAR(40) NOT NULL,
    relationship         VARCHAR(200) NOT NULL,
    withdrawal_limit     INT,
    is_emergency_contact BIT(1) DEFAULT FALSE,
    start_date           DATE,
    end_date             DATE,
    created              DATETIME NOT NULL,
    updated              DATETIME NOT NULL,
    customer_id          BINARY(16) REFERENCES customer,
    idx                  INT   DEFAULT 0,
    INDEX contact_customer_id_idx(customer_id)
);