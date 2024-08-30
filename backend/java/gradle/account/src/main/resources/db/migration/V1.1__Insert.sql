INSERT INTO
    account (
        id,
        version,
        balance,
        rate_of_interest,
        category,
        state,
        overdraft,
        withdrawal_limit,
        created,
        updated,
        customer_id
    )
VALUES
    (
        UUID_TO_BIN('20000000-0000-0000-0000-000000000000'),
        0,
        1000,
        0.05,
        'SAVINGS',
        'ACTIVE',
        0,
        1000,
        NOW(),
        NOW(),
        UUID_TO_BIN('00000000-0000-0000-0000-000000000000')
    );