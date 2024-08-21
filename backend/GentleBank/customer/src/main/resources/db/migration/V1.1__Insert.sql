

INSERT INTO address (id, street, house_number, zip_code, state, city)
VALUES
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000001'), 'Namurstraße', '4', '70374', 'Baden Württemberg', 'Stuttgart');


-- Beispiele für Customer
INSERT INTO customer (id, version, last_name, first_name, email, birth_date, contact_options, gender, marital_status, address_id, created, updated)
VALUES
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), 0, 'Gyamgi', 'Caleb', 'caleb_g@outlook.de', '1990-05-03', 'EMAIL,PHONE,MAIL,SMS', 'MALE', 'SINGLE', UUID_TO_BIN('10000000-0000-0000-0000-000000000001'), NOW(), NOW());
