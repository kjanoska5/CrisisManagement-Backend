-- Databases created by Hibernate before Flyway are baselined at version 2.
-- Add these columns idempotently so both fresh and pre-Flyway schemas converge.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN IF NOT EXISTS google_sub VARCHAR(255);

UPDATE users SET auth_provider = 'LOCAL' WHERE auth_provider IS NULL;

ALTER TABLE users
    ALTER COLUMN auth_provider TYPE VARCHAR(20),
    ALTER COLUMN auth_provider SET DEFAULT 'LOCAL',
    ALTER COLUMN auth_provider SET NOT NULL,
    ALTER COLUMN role TYPE VARCHAR(50),
    ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE reports
    ALTER COLUMN status TYPE VARCHAR(50);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'users'::regclass AND contype = 'u'
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid = 'users'::regclass AND attname = 'email')]
    ) THEN
        ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'users'::regclass AND contype = 'u'
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid = 'users'::regclass AND attname = 'google_sub')]
    ) THEN
        ALTER TABLE users ADD CONSTRAINT uk_users_google_sub UNIQUE (google_sub);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_reports_user'
    ) THEN
        ALTER TABLE reports ADD CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_reports_threat_type'
    ) THEN
        ALTER TABLE reports ADD CONSTRAINT fk_reports_threat_type FOREIGN KEY (threat_type_id) REFERENCES threat_types(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'users'::regclass AND contype = 'u'
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid = 'users'::regclass AND attname = 'medical_card_id')]
    ) THEN
        ALTER TABLE users ADD CONSTRAINT uk_users_medical_card UNIQUE (medical_card_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'users'::regclass AND contype = 'c' AND conname = 'ck_users_auth_provider'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT ck_users_auth_provider CHECK (auth_provider IN ('LOCAL', 'GOOGLE'));
    END IF;
END $$;

-- Deleting a medical card must not delete its owning user.
DO $$
DECLARE existing_fk RECORD;
BEGIN
    FOR existing_fk IN
        SELECT conname FROM pg_constraint
        WHERE conrelid = 'users'::regclass AND contype = 'f'
          AND confrelid = 'medical_cards'::regclass
    LOOP
        EXECUTE format('ALTER TABLE users DROP CONSTRAINT %I', existing_fk.conname);
    END LOOP;
    ALTER TABLE users ADD CONSTRAINT fk_users_medical_card
        FOREIGN KEY (medical_card_id) REFERENCES medical_cards(id) ON DELETE SET NULL;
END $$;

-- The development seed user gets a valid BCrypt hash for "password123"
UPDATE users
SET password_hash = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGKKGxxa21r5X/O.1bJW'
WHERE email = 'test.citizen@example.com' AND (password_hash = 'stub' OR password_hash IS NULL);

INSERT INTO users (id, email, password_hash, auth_provider, role, first_name, last_name, created_at)
VALUES (
           'a4bab6f2-60fa-461a-b7b5-746b0e883896',
           'test.citizen@example.com',
           '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGKKGxxa21r5X/O.1bJW',
           'LOCAL',
           'CITIZEN',
           'john',
           'doe',
           NOW()
       )
ON CONFLICT DO NOTHING;

INSERT INTO threat_types (id, name, icon)
SELECT seed.id, seed.name, seed.icon
FROM (VALUES
    ('11111111-1111-1111-1111-111111111111'::uuid, 'fire', 'fire_icon'),
    ('22222222-2222-2222-2222-222222222222'::uuid, 'flood', 'flood_icon'),
    ('33333333-3333-3333-3333-333333333333'::uuid, 'traffic accident', 'accident_icon'),
    ('44444444-4444-4444-4444-444444444444'::uuid, 'infrastructure failure', 'infrastructure_icon')
) AS seed(id, name, icon)
WHERE NOT EXISTS (
    SELECT 1 FROM threat_types existing WHERE existing.name = seed.name
)
ON CONFLICT DO NOTHING;
