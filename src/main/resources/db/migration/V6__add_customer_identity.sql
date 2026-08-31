ALTER TABLE users
    ADD COLUMN IF NOT EXISTS customer_number VARCHAR(8);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS national_id VARCHAR(11);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS phone VARCHAR(10);


-- Eski geliştirme kullanıcıları varsa migration patlamasın diye
-- geçici benzersiz değerler veriyoruz.
-- Zaten birazdan bu eski dataları komple temizleyeceğiz.

UPDATE users
SET customer_number = LPAD(
        (10000000 + id)::TEXT,
        8,
        '0'
                      )
WHERE customer_number IS NULL;

UPDATE users
SET national_id = LPAD(
        (90000000000 + id)::TEXT,
        11,
        '0'
                  )
WHERE national_id IS NULL;

UPDATE users
SET phone = LPAD(
        (5000000000 + id)::TEXT,
        10,
        '0'
            )
WHERE phone IS NULL;


ALTER TABLE users
    ALTER COLUMN customer_number SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN national_id SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;


CREATE UNIQUE INDEX IF NOT EXISTS uq_users_customer_number
    ON users(customer_number);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_national_id
    ON users(national_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_phone
    ON users(phone);