INSERT INTO account ( id, version, balance, rate_of_interest, category, state, overdraft, withdrawal_limit, created, updated, customer_id)
VALUES
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000000'), 0, 1000, 0.05, 'SAVINGS', 'ACTIVE', 0, 1000, NOW(), NOW(),     UUID_TO_BIN('00000000-0000-0000-0000-000000000000')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000001'), 0, 5000, 0.03, 'CHECKING', 'ACTIVE', 500, 2000, NOW(), NOW(),  UUID_TO_BIN('00000000-0000-0000-0000-000000000005')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000002'), 0, 15000, 0.04, 'CREDIT', 'BLOCKED', 1000, 5000, NOW(), NOW(), UUID_TO_BIN('00000000-0000-0000-0000-000000000002')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000003'), 0, 10000, 0.02, 'DEPOSIT', 'CLOSED', 0, 3000, NOW(), NOW(),    UUID_TO_BIN('00000000-0000-0000-0000-000000000003')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000004'), 0, 20000, 0.05, 'INVESTMENT', 'ACTIVE', 0, 4000, NOW(), NOW(), UUID_TO_BIN('00000000-0000-0000-0000-000000000004')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000005'), 0, 3000, 0.06, 'LOAN', 'BLOCKED', 500, 1000, NOW(), NOW(),     UUID_TO_BIN('00000000-0000-0000-0000-000000000005')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000006'), 0, 7500, 0.01, 'SAVINGS', 'CLOSED', 0, 1500, NOW(), NOW(),     UUID_TO_BIN('00000000-0000-0000-0000-000000000026')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000007'), 0, 12500, 0.03, 'CHECKING', 'ACTIVE', 1000, 2500, NOW(), NOW(),UUID_TO_BIN('00000000-0000-0000-0000-000000000007')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000008'), 0, 50000, 0.04, 'CREDIT', 'BLOCKED', 2000, 6000, NOW(), NOW(), UUID_TO_BIN('00000000-0000-0000-0000-000000000008')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000009'), 0, 35000, 0.05, 'DEPOSIT', 'CLOSED', 0, 4500, NOW(), NOW(),    UUID_TO_BIN('00000000-0000-0000-0000-000000000025')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000010'), 0, 40000, 0.02, 'INVESTMENT', 'ACTIVE', 0, 3500, NOW(), NOW(), UUID_TO_BIN('00000000-0000-0000-0000-000000000025')),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000011'), 1, 0, 0.02, 'INVESTMENT', 'ACTIVE', 0, 3500, NOW(), NOW(),     UUID_TO_BIN('00000000-0000-0000-0000-000000000026'));
