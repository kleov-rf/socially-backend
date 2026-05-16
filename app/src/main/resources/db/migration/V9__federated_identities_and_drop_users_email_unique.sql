ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_email_unique;

CREATE TABLE federated_identities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    issuer TEXT NOT NULL,
    subject TEXT NOT NULL,
    email TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT federated_identities_issuer_subject_unique UNIQUE (issuer, subject)
);
