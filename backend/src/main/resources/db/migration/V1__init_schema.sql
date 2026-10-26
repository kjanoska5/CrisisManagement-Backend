CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE medical_cards (
                               id BIGSERIAL PRIMARY KEY,
                               blood_type VARCHAR(255),
                               allergies VARCHAR(255),
                               chronic_diseases VARCHAR(255),
                               medications VARCHAR(255),
                               emergency_contact VARCHAR(255)
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
                       google_sub VARCHAR(255) UNIQUE,
                       role VARCHAR(50),
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       medical_card_id BIGINT UNIQUE,
                       CONSTRAINT fk_users_medical_card FOREIGN KEY (medical_card_id) REFERENCES medical_cards(id) ON DELETE CASCADE
);

CREATE TABLE threat_types (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(255) NOT NULL,
                              icon VARCHAR(255)
);

CREATE TABLE reports (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id UUID NOT NULL,
                         threat_type_id UUID NOT NULL,
                         location geography(Point, 4326) NOT NULL,
                         photo_url VARCHAR(255),
                         comment TEXT,
                         source VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
                         status VARCHAR(50) NOT NULL DEFAULT 'VERIFYING',
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                         CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT fk_reports_threat_type FOREIGN KEY (threat_type_id) REFERENCES threat_types(id)
);