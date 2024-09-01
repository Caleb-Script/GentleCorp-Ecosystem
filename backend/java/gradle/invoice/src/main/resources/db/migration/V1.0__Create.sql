
CREATE TABLE invoice (
    id UUID PRIMARY KEY USING INDEX TABLESPACE invoice_tablespace,
    version          integer NOT NULL DEFAULT 0,
    amount DECIMAL(10, 2) NOT NULL,
    due_date date,
    type varchar(90) ,
    account_id UUID,
    created timestamp NOT NULL,
     modified timestamp NOT NULL
) TABLESPACE invoice_tablespace;

CREATE TABLE IF NOT EXISTS payment (
    id uuid PRIMARY KEY USING INDEX TABLESPACE invoice_tablespace,
    amount DECIMAL(10, 2) NOT NULL,
    created timestamp NOT NULL,
    invoice_id uuid REFERENCES invoice,
    idx integer NOT NULL DEFAULT 0
) TABLESPACE invoice_tablespace;