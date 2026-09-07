CREATE TABLE saved_recipients (
                                  id BIGSERIAL PRIMARY KEY,

                                  owner_user_id BIGINT NOT NULL,
                                  recipient_account_id BIGINT NOT NULL,

                                  nickname VARCHAR(40) NOT NULL,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_saved_recipients_owner
                                      FOREIGN KEY (owner_user_id)
                                          REFERENCES users(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_saved_recipients_account
                                      FOREIGN KEY (recipient_account_id)
                                          REFERENCES accounts(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT uq_saved_recipients_owner_account
                                      UNIQUE (owner_user_id, recipient_account_id)
);

CREATE INDEX idx_saved_recipients_owner_user_id
    ON saved_recipients(owner_user_id);