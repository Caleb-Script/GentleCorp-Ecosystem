
CREATE ROLE invoice_db_user LOGIN PASSWORD 'GentleCorp21.08.2024';

CREATE DATABASE invoice_db;

GRANT ALL ON DATABASE invoice_db TO invoice_db_user;

CREATE TABLESPACE invoice_tablespace OWNER invoice_db_user LOCATION '/var/lib/postgresql/tablespace/invoice';
CREATE TABLESPACE invoice_tablespace OWNER invoice_db_user LOCATION '/Users/gentlebookpro/Projekte/GentleCorp-Ecosystem/volumes/tablespace/invoice';
