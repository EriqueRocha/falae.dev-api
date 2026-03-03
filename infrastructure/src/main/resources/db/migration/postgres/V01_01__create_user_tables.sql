--admins table
CREATE TABLE IF NOT EXISTS admins (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--authors table
CREATE TABLE IF NOT EXISTS authors (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    user_name VARCHAR(40) NOT NULL UNIQUE,
    git_hub VARCHAR(255),
    title VARCHAR(255),
    profile_image_url VARCHAR(255),
    bio VARCHAR(500),
    bug_coins INT NOT NULL DEFAULT 0,
    google_login BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--forum configuration table
CREATE TABLE IF NOT EXISTS forum_config (
    id UUID PRIMARY KEY,
    amount_coins_start INT NOT NULL,
    coins_first_article INT NOT NULL,
    coins_first_comment INT NOT NULL,
    coins_first_topic INT NOT NULL,
    coins_per_topic INT NOT NULL,
    coins_per_comment INT NOT NULL,
    coins_per_article INT NOT NULL,
    store_unlocked BOOLEAN NOT NULL,
    user_title_unlocked BOOLEAN NOT NULL,
    article_creation_unlocked BOOLEAN NOT NULL,
    topic_creation_unlocked BOOLEAN NOT NULL,
    comment_unlocked BOOLEAN NOT NULL,
    email_verification_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
