--articles table
CREATE TABLE IF NOT EXISTS articles (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES authors(id),
    is_markdown BOOLEAN,
    title VARCHAR(255),
    slug VARCHAR(255),
    cover_image VARCHAR(255),
    original_post TEXT,
    tags TEXT[],
    description VARCHAR(500),
    url_article_content VARCHAR(255),
    likes_count INT NOT NULL DEFAULT 0,
    dislikes_count INT NOT NULL DEFAULT 0,
    saves_count INT NOT NULL DEFAULT 0,
    comments_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--article image paths
CREATE TABLE IF NOT EXISTS article_entity_image_paths (
    article_entity_id UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    image_paths VARCHAR(255)
);

--article likes
CREATE TABLE IF NOT EXISTS clients_liked_article (
    article_id UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, client_id)
);

--article dislikes
CREATE TABLE IF NOT EXISTS clients_disliked_article (
    article_id UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, client_id)
);

--article saves
CREATE TABLE IF NOT EXISTS clients_saved_article (
    article_id UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, client_id)
);

--topics table
CREATE TABLE IF NOT EXISTS topics (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES authors(id),
    title VARCHAR(255),
    slug VARCHAR(255),
    topic_content VARCHAR(10000),
    tags TEXT[],
    likes_count INT NOT NULL DEFAULT 0,
    dislikes_count INT NOT NULL DEFAULT 0,
    comments_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--topic likes
CREATE TABLE IF NOT EXISTS authors_liked_topic (
    topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (topic_id, author_id)
);

--topic dislikes
CREATE TABLE IF NOT EXISTS authors_disliked_topic (
    topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (topic_id, author_id)
);

--comments table
CREATE TABLE IF NOT EXISTS comments (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES authors(id),
    article_id UUID REFERENCES articles(id) ON DELETE CASCADE,
    topic_id UUID REFERENCES topics(id) ON DELETE CASCADE,
    parent_id UUID REFERENCES comments(id) ON DELETE CASCADE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    path VARCHAR(1000),
    depth INTEGER DEFAULT 0,
    comment_content VARCHAR(3600),
    tags TEXT[],
    likes INT NOT NULL DEFAULT 0,
    dislikes INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--comment likes
CREATE TABLE IF NOT EXISTS comment_likes (
    comment_id UUID NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (comment_id, author_id)
);

--comment dislikes
CREATE TABLE IF NOT EXISTS comment_dislikes (
    comment_id UUID NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (comment_id, author_id)
);
