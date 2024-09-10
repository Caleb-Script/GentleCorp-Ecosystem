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
--     cd .extras\compose\db\mysql
--     docker compose up
-- (1) 2. PowerShell:
--     cd .extras\compose\db\mysql
--     docker compose exec db bash
--         mysql --user=root --password=p < /sql/create-db-shopping-cart-db-user.sql
--         exit
--     docker compose down


CREATE USER IF NOT EXISTS 'shopping_cart_db_user'@'localhost' IDENTIFIED BY 'GentleCorp21.08.2024';
ALTER USER 'shopping_cart_db_user' @'localhost' IDENTIFIED WITH mysql_native_password BY 'GentleCorp21.08.2024';

FLUSH PRIVILEGES;

GRANT USAGE ON *.* TO 'shopping_cart_db_user';
GRANT USAGE ON *.* TO 'shopping_cart_db_user'@'localhost';

CREATE DATABASE IF NOT EXISTS `shopping_cart_db` CHARACTER SET utf8;

GRANT ALL PRIVILEGES ON `shopping_cart_db`.* to 'shopping_cart_db_user';
GRANT ALL PRIVILEGES ON `shopping_cart_db`.* to 'shopping_cart_db_user'@'localhost';

CREATE TABLESPACE shopping_cart_tablespace ADD DATAFILE 'shopping_cart_tablespace.ibd' ENGINE=INNODB;

mysql -u shopping_cart_db_user -p
GentleCorp21.08.2024