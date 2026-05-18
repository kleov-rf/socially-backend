ALTER TABLE donations
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN description SET NOT NULL,
    ALTER COLUMN location_address SET NOT NULL,
    ALTER COLUMN location_latitude SET NOT NULL,
    ALTER COLUMN location_longitude SET NOT NULL;
