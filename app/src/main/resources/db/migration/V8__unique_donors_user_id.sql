ALTER TABLE donors
    ADD CONSTRAINT donors_user_id_unique UNIQUE (user_id);
