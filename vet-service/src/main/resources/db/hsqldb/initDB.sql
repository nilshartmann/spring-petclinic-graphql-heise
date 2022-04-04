DROP TABLE vet_specialties IF EXISTS CASCADE;
DROP TABLE vets IF EXISTS CASCADE;
DROP TABLE specialties IF EXISTS CASCADE;

CREATE TABLE vets
(
    id         INTEGER IDENTITY PRIMARY KEY,
    first_name VARCHAR(30),
    last_name  VARCHAR(30)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties
(
    id   INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties
(
    vet_id       INTEGER NOT NULL,
    specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties
    ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties
    ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);
