CREATE TABLE IF NOT EXISTS people
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    family_id INTEGER NOT NULL
);