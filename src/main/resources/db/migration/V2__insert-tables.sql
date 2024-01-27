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
VALUES ('e3480b72-c82c-45cd-beb7-a8155b04b7e0', 'TOP_UP', 'CARD', 10.00, 'USD', NOW(), 'EN', 'https://example.com/webhook1', 1, 1, 'SUCCESS', 'Message1'),
       ('69ef394d-ad72-47b9-b5be-2445181b7532', 'TOP_UP', 'CARD', 20.00, 'EUR', NOW(), 'EN', 'https://example.com/webhook2', 2, 2, 'SUCCESS', 'Message2'),
       ('7f85bf5f-1b93-4bee-914a-a59264e6a344', 'PAY_OUT', 'CARD', 30.00, 'GBP', NOW(), 'EN', 'https://example.com/webhook3', 3, 3, 'FAILED', 'Message3'),
       ('57c00c84-1580-43f7-8fea-d89b68d7fcd0', 'PAY_OUT', 'CARD', 40.00, 'JPY', NOW(), 'EN', 'https://example.com/webhook4', 4, 4, 'FAILED', 'Message4');

-- Заполнение таблицы webhooks_history
INSERT INTO webhooks_history (notification_url, created_at, response, transaction_id)
VALUES ('https://example.com/webhook1', NOW(), 'Response1', 1),
       ('https://example.com/webhook2', NOW(), 'Response2', 2),
       ('https://example.com/webhook3', NOW(), 'Response3', 3),
       ('https://example.com/webhook4', NOW(), 'Response4', 4);