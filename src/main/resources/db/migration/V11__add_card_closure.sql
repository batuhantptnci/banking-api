ALTER TABLE cards
DROP CONSTRAINT chk_cards_status;

ALTER TABLE cards
    ADD CONSTRAINT chk_cards_status
        CHECK (
            card_status IN (
                            'ACTIVE',
                            'FROZEN',
                            'CLOSED'
                )
            );

ALTER TABLE cards
    ADD COLUMN closed_at TIMESTAMP NULL;

ALTER TABLE cards
DROP CONSTRAINT uq_cards_account;

CREATE INDEX idx_cards_account_id
    ON cards(account_id);

CREATE UNIQUE INDEX uq_cards_open_account
    ON cards(account_id)
    WHERE card_status IN (
        'ACTIVE',
        'FROZEN'
    );