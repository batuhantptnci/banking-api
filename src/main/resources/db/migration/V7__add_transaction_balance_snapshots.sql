ALTER TABLE transactions
    ADD COLUMN source_balance_after NUMERIC(19, 2),
    ADD COLUMN target_balance_after NUMERIC(19, 2);