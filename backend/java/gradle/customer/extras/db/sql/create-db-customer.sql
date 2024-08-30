-- Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

-- (1) PowerShell:
--     cd extras\compose\mysql
--     docker compose up
-- (1) 2. PowerShell:
--     cd extras\compose\mysql
--     docker compose exec db bash
--         mysql --user=root --password=p < /sql/create-db-customer.sql
--         exit
--     docker compose down

-- mysqlsh ist *NICHT* im Docker-Image enthalten: https://dev.mysql.com/doc/refman/8.2/en/mysql.html

-- 1. Erstellen Sie den Benutzer, falls er nicht bereits existiert, und setzen Sie das Passwort.
CREATE USER IF NOT EXISTS 'customer-db-user'@'localhost' IDENTIFIED BY 'GentleCorp21.08.2024';

-- 2. Erteilen Sie dem Benutzer grundlegende Zugriffsrechte.
GRANT USAGE ON *.* TO 'customer-db-user'@'localhost';

-- 3. Erstellen Sie die Datenbank, falls sie nicht bereits existiert, und legen Sie die Zeichencodierung fest.
CREATE DATABASE IF NOT EXISTS `customer-db` CHARACTER SET utf8;

-- 4. Erteilen Sie dem Benutzer alle Berechtigungen auf der neu erstellten Datenbank.
GRANT ALL ON `customer-db`.* TO 'customer-db-user'@'localhost';

-- 5. Erstellen Sie den Tablespace und fügen Sie eine Daten-Datei hinzu (optional, wenn Sie den Tablespace verwenden möchten).
CREATE TABLESPACE customer_tablespace ADD DATAFILE 'customer_tablespace.ibd' ENGINE=INNODB;


CREATE TABLESPACE customerspace OWNER customer_db_user LOCATION '/var/lib/postgresql/tablespace/customer';
CREATE TABLESPACE customer_tablespace OWNER customer_db_user LOCATION '/Users/gentlebookpro/Projekte/GentleCorp-Ecosystem/volumes/Tablespace/Customer';

