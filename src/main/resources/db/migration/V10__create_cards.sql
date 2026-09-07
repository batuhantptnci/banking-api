CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,

                       user_id BIGINT NOT NULL,
                       account_id BIGINT NOT NULL,

                       card_type VARCHAR(20) NOT NULL,
                       card_status VARCHAR(20) NOT NULL,

                       last_four VARCHAR(4) NOT NULL,

                       expiry_month INTEGER NOT NULL,
                       expiry_year INTEGER NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_cards_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_cards_account
                           FOREIGN KEY (account_id)
                               REFERENCES accounts(id)
                               ON DELETE CASCADE,

                       CONSTRAINT uq_cards_account
                           UNIQUE (account_id),

                       CONSTRAINT chk_cards_last_four
                           CHECK (last_four ~ '^[0-9]{4}$'),

    CONSTRAINT chk_cards_expiry_month
        CHECK (expiry_month BETWEEN 1 AND 12),

    CONSTRAINT chk_cards_type
        CHECK (card_type IN ('DEBIT')),

    CONSTRAINT chk_cards_status
        CHECK (card_status IN ('ACTIVE', 'FROZEN'))
);

CREATE INDEX idx_cards_user_id
    ON cards(user_id);