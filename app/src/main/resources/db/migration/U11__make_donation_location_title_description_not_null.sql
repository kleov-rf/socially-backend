ALTER TABLE donations
    ALTER COLUMN title DROP NOT NULL,
    ALTER COLUMN description DROP NOT NULL,
    ALTER COLUMN location_address DROP NOT NULL,
    ALTER COLUMN location_latitude DROP NOT NULL,
    ALTER COLUMN location_longitude DROP NOT NULL;
