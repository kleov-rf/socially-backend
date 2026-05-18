ALTER TABLE donations
    ADD COLUMN location_address TEXT,
    ADD COLUMN location_latitude DOUBLE PRECISION,
    ADD COLUMN location_longitude DOUBLE PRECISION;
