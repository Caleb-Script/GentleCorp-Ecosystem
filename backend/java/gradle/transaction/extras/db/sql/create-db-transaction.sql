
CREATE ROLE transaction_db_user LOGIN PASSWORD 'GentleCorp21.08.2024';

CREATE DATABASE transaction_db;

GRANT ALL ON DATABASE transaction_db TO transaction_db_user;

CREATE TABLESPACE transaction_tablespace OWNER transaction_db_user LOCATION '/var/lib/postgresql/tablespace/transaction';
CREATE TABLESPACE transaction_tablespace OWNER transaction_db_user LOCATION '/Users/gentlebookpro/Projekte/GentleCorp-Ecosystem/volumes/tablespace/transaction';
