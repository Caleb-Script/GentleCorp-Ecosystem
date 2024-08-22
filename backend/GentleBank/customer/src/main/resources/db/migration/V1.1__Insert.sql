
INSERT INTO
    address (id, street, house_number, zip_code, state, city)
VALUES
    (
        UUID_TO_BIN('10000000-0000-0000-0000-000000000000'),
        'Namurstraße',
        '4',
        '70374',
        'Baden Württemberg',
        'Stuttgart'
    ),
    (
        UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),
        'Namurstraße',
        '4',
        '70374',
        'Baden Württemberg',
        'Stuttgart'
    ),
    (
        UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),
        'Namurstraße',
        '4',
        '70374',
        'Baden Württemberg',
        'Stuttgart'
    ),
    (
        UUID_TO_BIN('10000000-0000-0000-0000-000000000003'),
        'Namurstraße',
        '4',
        '70374',
        'Baden Württemberg',
        'Stuttgart'
    ),
    (
        UUID_TO_BIN('10000000-0000-0000-0000-000000000004'),
        'Namurstraße',
        '4',
        '70374',
        'Baden Württemberg',
        'Stuttgart'
    );

-- Beispiele für Customer
INSERT INTO
    customer (
        id,
        version,
        last_name,
        first_name,
        email,
        birth_date,
        contact_options,
        gender,
        marital_status,
        address_id,
        created,
        updated,
        username
    )
VALUES
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000000'),
        0,
        'Admin',
        'Caleb',
        'admin@gentlecorp.com',
        '1990-05-03',
        'EMAIL,PHONE,MAIL,SMS',
        'MALE',
        'MARRIED',
        UUID_TO_BIN('10000000-0000-0000-0000-000000000000'),
        NOW(),
        NOW(),
        'admin'
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),
        0,
        'User',
        'Caleb',
        'user@gentlecorp.com',
        '1990-05-03',
        'EMAIL,PHONE,MAIL,SMS',
        'MALE',
        'SINGLE',
        UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),
        NOW(),
        NOW(),
        'user'
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000002'),
        0,
        'Jefferson',
        'Leroy',
        'leroy135@icloud.com',
        '1990-05-03',
        'EMAIL',
        'MALE',
        'SINGLE',
        UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),
        NOW(),
        NOW(),
        'leroy135'
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000003'),
        0,
        'Admin',
        'Rachel',
        'rae98@gentlecorp.com',
        '1990-05-03',
        'EMAIL,PHONE,MAIL,SMS',
        'FEMALE',
        'MARRIED',
        UUID_TO_BIN('10000000-0000-0000-0000-000000000003'),
        NOW(),
        NOW(),
        'admin2'
    ),
    (
        UUID_TO_BIN('00000000-0000-0000-0000-000000000004'),
        0,
        'Mustermannn',
        'Max',
        'leroy135 @icloud.com',
        '1990-05-03',
        'EMAIL,MAIL',
        'DIVERSE',
        'DIVORCED',
        UUID_TO_BIN('10000000-0000-0000-0000-000000000004'),
        NOW(),
        NOW(),
        'max'
    );