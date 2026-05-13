CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT NOT NULL,
    given_name TEXT,
    family_name TEXT,
    created_at TIMESTAMPTZ NOT NULL
);
