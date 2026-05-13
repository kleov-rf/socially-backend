ALTER TABLE donations
    DROP CONSTRAINT IF EXISTS fk_donations_donor_id;

ALTER TABLE donations
    DROP COLUMN IF EXISTS donor_id;

DROP TABLE IF EXISTS donors;
