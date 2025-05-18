-- Таблица ролей
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Вставка ролей
INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN'),
('ROLE_MODERATOR');

ALTER TABLE users
    ADD COLUMN role_id BIGINT;

ALTER TABLE users
   ADD CONSTRAINT fk_role
   FOREIGN KEY (role_id) REFERENCES roles(id);