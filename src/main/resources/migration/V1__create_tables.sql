-- Таблиця posts
CREATE TABLE posts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    text TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблиця comments
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблиця likes
CREATE TABLE likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
