CREATE TABLE donors
(
    id          UUID PRIMARY KEY,
    user_id     UUID                     NOT NULL,
    email       TEXT                     NOT NULL,
    given_name  TEXT,
    family_name TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_donors_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER TABLE donations
    ADD COLUMN donor_id UUID;

ALTER TABLE donations
    ADD CONSTRAINT fk_donations_donor_id FOREIGN KEY (donor_id) REFERENCES donors (id);

ALTER TABLE donations
    ALTER COLUMN donor_id SET NOT NULL;
