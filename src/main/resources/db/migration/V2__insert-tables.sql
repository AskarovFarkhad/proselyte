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
INSERT INTO transactions (transaction_id, transaction_type, payment_method, amount, currency, created_at, language, notification_url, customer_id, card_id,
                          status, message)
VALUES ('e3480b72-c82c-45cd-beb7-a8155b04b7e0', 'TOP_UP', 'CARD', 10.00, 'USD', NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 1, 1, 'SUCCESS', 'Message1'),
       ('69ef394d-ad72-47b9-b5be-2445181b7532', 'TOP_UP', 'CARD', 20.00, 'EUR', NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/topup', 2, 2, 'SUCCESS', 'Message2'),
       ('7f85bf5f-1b93-4bee-914a-a59264e6a344', 'PAY_OUT', 'CARD', 30.00, 'GBP', NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 3, 3, 'FAILED', 'Message3'),
       ('57c00c84-1580-43f7-8fea-d89b68d7fcd0', 'PAY_OUT', 'CARD', 40.00, 'JPY', NOW(), 'EN',
        'https://65bbd4bf52189914b5bd2c40.mockapi.io/api/v1/webhook/transaction/payout', 4, 4, 'FAILED', 'Message4');

-- Вставка данных в таблицу merchants
INSERT INTO merchants (merchant_id, secret_key, enabled)
VALUES ('PROSELYTE', 'b2eeea3e27834b7499dd7e01143a23dd', true),
       ('merchant2', 'secret2', true),
       ('merchant3', 'secret3', false);

-- Вставка данных в таблицу wallets
INSERT INTO wallets (currency, balance, merchant_id)
VALUES ('USD', 100.00, 'PROSELYTE'),
       ('EUR', 200.00, 'PROSELYTE'),
       ('USD', 50.00, 'merchant2');