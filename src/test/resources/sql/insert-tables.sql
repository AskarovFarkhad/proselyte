CREATE TABLE merchants
(
    merchant_id VARCHAR(64)   NOT NULL UNIQUE,
    secret_key  VARCHAR(2048) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE
);

CREATE TABLE wallets
(
    wallet_id   SERIAL PRIMARY KEY,
    currency    VARCHAR(8)  NOT NULL,
    balance     NUMERIC,
    merchant_id VARCHAR(64) NOT NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchants (merchant_id)
);

CREATE TABLE customers
(
    customer_id SERIAL PRIMARY KEY,
    first_name  VARCHAR(64) NOT NULL,
    last_name   VARCHAR(64) NOT NULL,
    country     VARCHAR(64) NOT NULL
);

CREATE TABLE cards
(
    card_id     SERIAL PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    exp_date    DATE        NOT NULL,
    cvv         INT         NOT NULL,
    customer_id INT         NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);

CREATE TABLE transactions
(
    id               SERIAL PRIMARY KEY,
    transaction_id   UUID        NOT NULL,
    transaction_type VARCHAR(8)  NOT NULL,
    payment_method   VARCHAR(8)  NOT NULL,
    amount           NUMERIC     NOT NULL,
    currency         VARCHAR(4)  NOT NULL,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP,
    language         VARCHAR(2)  NOT NULL,
    notification_url VARCHAR(128),
    customer_id      INT,
    card_id          INT,
    merchant_id      VARCHAR(64),
    status           VARCHAR(16) NOT NULL,
    message          VARCHAR(128),
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    FOREIGN KEY (card_id) REFERENCES cards (card_id)
);

CREATE TABLE webhooks_history
(
    id               SERIAL PRIMARY KEY,
    notification_url VARCHAR(128),
    created_at       TIMESTAMP NOT NULL,
    response         TEXT      NOT NULL,
    request          TEXT      NOT NULL
);

-- Заполнение таблицы customers
INSERT INTO customers (first_name, last_name, country)
VALUES ('John', 'Doe', 'USA'),
       ('Jane', 'Smith', 'UK'),
       ('Michael', 'Johnson', 'Canada'),
       ('Emily', 'Brown', 'Australia');

-- Заполнение таблицы cards
INSERT INTO cards (card_number, exp_date, cvv, customer_id)
VALUES ('1111111111111111', '2023-01-01', 123, 1),
       ('2222222222222222', '2024-02-01', 456, 2),
       ('3333333333333333', '2025-03-01', 789, 3),
       ('4444444444444444', '2026-04-01', 987, 4);

-- Заполнение таблицы transactions
INSERT INTO transactions (transaction_id, transaction_type, payment_method, amount, currency, created_at, updated_at, language, notification_url, customer_id,
                          card_id, merchant_id, status, message)
VALUES ('e3480b72-c82c-45cd-beb7-a8155b04b7e0', 'TOP_UP', 'CARD', 10.00, 'USD', '2023-02-16T09:12:34.413', '2023-02-16T09:12:34.413', 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 1, 1, 'PROSELYTE', 'SUCCESS', 'OK'),

       ('69ef394d-ad72-47b9-b5be-2445181b7532', 'TOP_UP', 'CARD', 20.24, 'EUR', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 2, 2, 'PROSELYTE', 'SUCCESS', 'OK'),

       ('69ef394d-ad72-47b9-b5be-2445181b7522', 'TOP_UP', 'CARD', 200.54, 'USD', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 2, 4, 'PROSELYTE', 'FAILED', 'FAIL'),

       ('69ef394d-ad72-47b9-b5be-2445181b7532', 'TOP_UP', 'CARD', 250, 'USD', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 2, 4, 'PROSELYTE', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('69ef394d-ad72-47b9-b5be-2445181b7512', 'TOP_UP', 'CARD', 125, 'USD', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 2, 4, 'PROSELYTE', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('7f85bf5f-1b93-4bee-914a-a59264e6a344', 'PAY_OUT', 'CARD', 30.00, 'GBP', '2023-02-16T09:12:34.413', '2023-02-16T09:12:34.413', 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 3, 3, 'PROSELYTE', 'FAILED', 'FAIL'),

       ('57c00c84-1580-43f7-8fea-d89b68d7fcd0', 'PAY_OUT', 'CARD', 40.12, 'JPY', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'PROSELYTE', 'FAILED', 'FAIL'),

       ('57c00c84-1580-43f7-8fea-d89b68d7fcd0', 'PAY_OUT', 'CARD', 50, 'USD', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'merchant2', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('57c00c84-1580-43f7-8fea-d89b68d7fcd1', 'PAY_OUT', 'CARD', 250, 'EUR', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'PROSELYTE', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('57c00c84-1580-43f7-8fea-d89b68d7fcd2', 'PAY_OUT', 'CARD', 150, 'EUR', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'PROSELYTE', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('57c00c84-1580-43f7-8fea-d89b68d7fcd2', 'PAY_OUT', 'CARD', 1000, 'JPY', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'PROSELYTE', 'IN_PROGRESS', 'IN_PROGRESS'),

       ('57c00c84-1580-43f7-8fea-d89b48d7fcd0', 'PAY_OUT', 'CARD', 1500.22, 'EUR', NOW(), NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 3, 'PROSELYTE', 'FAILED', 'FAIL');

-- Заполнение таблицы merchants
INSERT INTO merchants (merchant_id, secret_key, enabled)
VALUES ('PROSELYTE', 'b2eeea3e27834b7499dd7e01143a23dd', true),
       ('merchant2', 'secret2', true),
       ('merchant3', 'secret3', false);

-- Заполнение таблицы wallets
INSERT INTO wallets (currency, balance, merchant_id)
VALUES ('USD', 100.00, 'PROSELYTE'),
       ('EUR', 200.00, 'PROSELYTE'),
       ('USD', 50.00, 'merchant2');