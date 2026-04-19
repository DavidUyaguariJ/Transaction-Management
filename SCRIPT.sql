-- =============================================
-- NovoBanco - Account Management Schema
-- SQL Server 2022
-- =============================================

CREATE TABLE customers (
    id            UNIQUEIDENTIFIER  NOT NULL DEFAULT NEWID() PRIMARY KEY,
    full_name     NVARCHAR(150)     NOT NULL,
    email         NVARCHAR(150)     NOT NULL UNIQUE,
    document_id   NVARCHAR(20)      NOT NULL UNIQUE,
    created_at    DATETIME2         NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE accounts (
    id             UNIQUEIDENTIFIER  NOT NULL DEFAULT NEWID() PRIMARY KEY,
	account_number BIGINT IDENTITY(1000,1) NOT NULL UNIQUE,
    customer_id    UNIQUEIDENTIFIER  NOT NULL,
    type           NVARCHAR(10)      NOT NULL,
    currency       NCHAR(3)          NOT NULL DEFAULT 'USD',
    balance        DECIMAL(19,4)     NOT NULL DEFAULT 0.0000,
    status         NVARCHAR(10)      NOT NULL DEFAULT 'ACTIVE',
    created_at     DATETIME2         NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at     DATETIME2         NOT NULL DEFAULT SYSUTCDATETIME(),

    CONSTRAINT fk_account_customer  FOREIGN KEY (customer_id)  REFERENCES customers(id),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_account_type     CHECK (type   IN ('SAVINGS','CHECKING')),
    CONSTRAINT chk_account_status   CHECK (status IN ('ACTIVE','BLOCKED','CLOSED')),
    CONSTRAINT chk_currency         CHECK (currency = 'USD')
);

CREATE TABLE transactions (
    id                 UNIQUEIDENTIFIER  NOT NULL DEFAULT NEWID() PRIMARY KEY,
    reference          NVARCHAR(36)      NOT NULL UNIQUE,
    account_id         UNIQUEIDENTIFIER  NOT NULL,
    related_account_id UNIQUEIDENTIFIER  NULL,
    type               NVARCHAR(15)      NOT NULL,
    amount             DECIMAL(19,4)     NOT NULL,
    balance_after      DECIMAL(19,4)     NOT NULL,
    status             NVARCHAR(10)      NOT NULL,
    description        NVARCHAR(255)     NULL,
    created_at         DATETIME2         NOT NULL DEFAULT SYSUTCDATETIME(),

    CONSTRAINT fk_transaction_account         FOREIGN KEY (account_id)         REFERENCES accounts(id),
    CONSTRAINT fk_transaction_related_account FOREIGN KEY (related_account_id) REFERENCES accounts(id),
	CONSTRAINT chk_balance_after_non_negative CHECK (balance_after >= 0),
    CONSTRAINT chk_transaction_type   CHECK (type   IN ('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT')),
    CONSTRAINT chk_transaction_status CHECK (status IN ('SUCCESS','FAILED','REVERSED')),
    CONSTRAINT chk_amount_positive    CHECK (amount > 0)
);

-- =============================================
-- ÍNDICES
-- =============================================

CREATE NONCLUSTERED INDEX idx_transactions_account_date
    ON transactions(account_id, created_at DESC);

CREATE UNIQUE NONCLUSTERED INDEX idx_transactions_reference
    ON transactions(reference);

CREATE NONCLUSTERED INDEX idx_transactions_account_type_date
    ON transactions(account_id, type, created_at DESC);

CREATE NONCLUSTERED INDEX idx_accounts_customer
    ON accounts(customer_id);

CREATE UNIQUE NONCLUSTERED INDEX idx_accounts_number
    ON accounts(account_number);