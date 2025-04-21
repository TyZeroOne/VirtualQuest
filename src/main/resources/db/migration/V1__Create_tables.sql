-- Пользователи
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    rating INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP
);

-- Категории
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Квесты
CREATE TABLE quests (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    content JSONB,
    difficulty VARCHAR(20) NOT NULL,
    started_count INT DEFAULT 0,
    completed_count INT DEFAULT 0,
    creator_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    points INT DEFAULT 0,
    published BOOLEAN DEFAULT FALSE
);

-- Шаги квеста
CREATE TABLE steps (
    id BIGSERIAL PRIMARY KEY,
    quest_id BIGINT REFERENCES quests(id),
    step_number INT NOT NULL,
    description TEXT NOT NULL,
    options JSONB,
    next_step_id BIGINT REFERENCES steps(id)
);

-- Прогресс прохождения
CREATE TABLE progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    quest_id BIGINT REFERENCES quests(id),
    current_step_id BIGINT REFERENCES steps(id),
    completed BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Рейтинги и отзывы
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    quest_id BIGINT REFERENCES quests(id),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Связь квестов и категорий (Many-to-Many)
CREATE TABLE quest_categories (
    quest_id BIGINT REFERENCES quests(id),
    category_id BIGINT REFERENCES categories(id),
    PRIMARY KEY (quest_id, category_id)
);