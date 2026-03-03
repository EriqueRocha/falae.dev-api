-- insert test admin
-- password: test123456
INSERT INTO admins (id, email, password, name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin1@falae.dev',
    '$2a$10$xi3eengxM5..Sa16AqgRU.cZ7lltDkacVlXLbYRqrzzttDVprHS06',
    'Admin One',
    NOW(),
    NOW()
);

-- insert initial forum configuration
INSERT INTO forum_config (
    id,
    amount_coins_start,
    coins_first_article,
    coins_first_comment,
    coins_first_topic,
    coins_per_topic,
    coins_per_comment,
    coins_per_article,
    store_unlocked,
    user_title_unlocked,
    article_creation_unlocked,
    topic_creation_unlocked,
    comment_unlocked,
    email_verification_required,
    created_at,
    updated_at
)
VALUES (
    gen_random_uuid(),
    10,     -- amount_coins_start
    50,     -- coins_first_article
    10,     -- coins_first_comment
    30,     -- coins_first_topic
    5,      -- coins_per_topic
    2,      -- coins_per_comment
    10,     -- coins_per_article
    FALSE,  -- store_unlocked
    TRUE,   -- user_title_unlocked
    TRUE,   -- article_creation_unlocked
    TRUE,   -- topic_creation_unlocked
    TRUE,   -- comment_unlocked
    FALSE,  -- email_verification_required
    NOW(),
    NOW()
);
