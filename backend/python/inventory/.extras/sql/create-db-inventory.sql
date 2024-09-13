-- mysql --user=root --password=p < /sql/create-db-inventory.sql
--         exit

CREATE USER IF NOT EXISTS 'inventory-db-user' IDENTIFIED BY 'GentleCorp21.08.2024';

-- 2. Erteilen Sie dem Benutzer grundlegende Zugriffsrechte.
GRANT USAGE ON *.* TO 'inventory-db-user';

-- 3. Erstellen Sie die Datenbank, falls sie nicht bereits existiert, und legen Sie die Zeichencodierung fest.
CREATE DATABASE IF NOT EXISTS `inventory-db` CHARACTER SET utf8;

-- 4. Erteilen Sie dem Benutzer alle Berechtigungen auf der neu erstellten Datenbank.
GRANT ALL PRIVILEGES ON `inventory-db`.* TO 'inventory-db-user' @'%';

-- 5. Erstellen Sie den Tablespace und fügen Sie eine Daten-Datei hinzu (optional, wenn Sie den Tablespace verwenden möchten).
CREATE TABLESPACE inventory_tablespace ADD DATAFILE 'inventory_tablespace.ibd' ENGINE=INNODB;
