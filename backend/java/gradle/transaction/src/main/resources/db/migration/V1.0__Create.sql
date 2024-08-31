
CREATE TABLE transaction
(
    id                UUID        PRIMARY KEY USING INDEX TABLESPACE transaction_tablespace,
    sender            UUID        ,
    receiver          UUID        ,
    version           INTEGER     NOT NULL DEFAULT 0,
    amount            DECIMAL(10, 2) NOT NULL,
--     status            VARCHAR(12) NOT NULL,
    created           TIMESTAMP,
    updated           TIMESTAMP
) TABLESPACE transaction_tablespace;
CREATE INDEX IF NOT EXISTS transaction_sender_idx ON transaction(sender) TABLESPACE transaction_tablespace;
