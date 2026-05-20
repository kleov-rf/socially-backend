CREATE TABLE donation_images
(
    id                 UUID PRIMARY KEY,
    donation_id        UUID        NOT NULL REFERENCES donations (id),
    storage_object_key TEXT        NOT NULL,
    content_type       VARCHAR(255) NOT NULL,
    size_bytes         BIGINT      NOT NULL,
    primary_flag       BOOLEAN     NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_donation_images_donation_id ON donation_images (donation_id);
