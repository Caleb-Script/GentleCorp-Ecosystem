
psql -U transaction_db_user transaction_db

CREATE SCHEMA IF NOT EXISTS transaction_schema AUTHORIZATION transaction_db_user;

ALTER ROLE transaction_db_user SET search_path = 'transaction_schema';