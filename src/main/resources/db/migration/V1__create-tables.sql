CREATE TABLE merchants
(
    merchant_id VARCHAR(64)   NOT NULL UNIQUE,
    secret_key  VARCHAR(2048) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE
);

CREATE TABLE wallets
(
    wallet_id  SERIAL PRIMARY KEY,
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
    customer         INT,
    card_data        INT,
    status           VARCHAR(16) NOT NULL,
    message          VARCHAR(32),
    FOREIGN KEY (customer) REFERENCES customers (customer_id),
    FOREIGN KEY (card_data) REFERENCES cards (card_id)
);

CREATE TABLE webhooks_history
(
    id               SERIAL PRIMARY KEY,
    notification_url VARCHAR(128),
    created_at       TIMESTAMP    NOT NULL,
    response         VARCHAR(128) NOT NULL,
    transaction_id   INT         NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);