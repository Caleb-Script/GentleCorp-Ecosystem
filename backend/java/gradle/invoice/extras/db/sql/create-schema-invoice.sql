
psql -U invoice_db_user invoice_db

CREATE SCHEMA IF NOT EXISTS invoice_schema AUTHORIZATION invoice_db_user;

ALTER ROLE invoice_db_user SET search_path = 'invoice_schema';