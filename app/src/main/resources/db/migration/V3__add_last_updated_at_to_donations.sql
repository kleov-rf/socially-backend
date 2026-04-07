ALTER TABLE donations
    ADD COLUMN last_updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE donations
SET last_updated_at = created_at;
