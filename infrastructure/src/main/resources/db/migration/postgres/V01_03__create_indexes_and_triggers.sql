-- user indexes
CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);
CREATE INDEX IF NOT EXISTS idx_authors_email ON authors(email);
CREATE INDEX IF NOT EXISTS idx_authors_user_name ON authors(user_name);

-- forum config index
CREATE INDEX IF NOT EXISTS idx_forum_config ON forum_config(id);

-- article indexes
CREATE INDEX IF NOT EXISTS idx_articles_author ON articles(author_id);
CREATE INDEX IF NOT EXISTS idx_articles_author_slug ON articles(author_id, slug);
CREATE INDEX IF NOT EXISTS idx_clients_liked_article_client ON clients_liked_article(client_id);
CREATE INDEX IF NOT EXISTS idx_clients_disliked_article_client ON clients_disliked_article(client_id);
CREATE INDEX IF NOT EXISTS idx_clients_saved_article_client ON clients_saved_article(client_id);

-- topic indexes
CREATE INDEX IF NOT EXISTS idx_topics_author ON topics(author_id);
CREATE INDEX IF NOT EXISTS idx_topics_author_slug ON topics(author_id, slug);
CREATE INDEX IF NOT EXISTS idx_authors_liked_topic_author ON authors_liked_topic(author_id);
CREATE INDEX IF NOT EXISTS idx_authors_disliked_topic_author ON authors_disliked_topic(author_id);

-- comment indexes
CREATE INDEX IF NOT EXISTS idx_comments_article_id ON comments(article_id);
CREATE INDEX IF NOT EXISTS idx_comments_topic_id ON comments(topic_id);
CREATE INDEX IF NOT EXISTS idx_comments_parent_id ON comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_comments_path ON comments(path);
CREATE INDEX IF NOT EXISTS idx_comments_depth ON comments(depth);
CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at);
CREATE INDEX IF NOT EXISTS idx_comment_likes_author ON comment_likes(author_id);
CREATE INDEX IF NOT EXISTS idx_comment_dislikes_author ON comment_dislikes(author_id);

-- gIN indexes for tag search
CREATE INDEX IF NOT EXISTS idx_articles_tags_gin ON articles USING GIN (tags);
CREATE INDEX IF NOT EXISTS idx_topics_tags_gin ON topics USING GIN (tags);
CREATE INDEX IF NOT EXISTS idx_comments_tags_gin ON comments USING GIN (tags);


-- Trigger function for updating article comments_count
CREATE OR REPLACE FUNCTION update_article_comments_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.article_id IS NOT NULL AND NEW.deleted = false THEN
            UPDATE articles SET comments_count = comments_count + 1 WHERE id = NEW.article_id;
        END IF;
    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.article_id IS NOT NULL AND OLD.deleted = false AND NEW.deleted = true THEN
            UPDATE articles SET comments_count = comments_count - 1 WHERE id = NEW.article_id;
        ELSIF NEW.article_id IS NOT NULL AND OLD.deleted = true AND NEW.deleted = false THEN
            UPDATE articles SET comments_count = comments_count + 1 WHERE id = NEW.article_id;
        END IF;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.article_id IS NOT NULL AND OLD.deleted = false THEN
            UPDATE articles SET comments_count = comments_count - 1 WHERE id = OLD.article_id;
        END IF;
    END IF;
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_article_comments_count
AFTER INSERT OR UPDATE OF deleted OR DELETE ON comments
FOR EACH ROW EXECUTE FUNCTION update_article_comments_count();

-- Trigger function for updating topic comments_count
CREATE OR REPLACE FUNCTION update_topic_comments_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.topic_id IS NOT NULL AND NEW.deleted = false THEN
            UPDATE topics SET comments_count = comments_count + 1 WHERE id = NEW.topic_id;
        END IF;
    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.topic_id IS NOT NULL AND OLD.deleted = false AND NEW.deleted = true THEN
            UPDATE topics SET comments_count = comments_count - 1 WHERE id = NEW.topic_id;
        ELSIF NEW.topic_id IS NOT NULL AND OLD.deleted = true AND NEW.deleted = false THEN
            UPDATE topics SET comments_count = comments_count + 1 WHERE id = NEW.topic_id;
        END IF;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.topic_id IS NOT NULL AND OLD.deleted = false THEN
            UPDATE topics SET comments_count = comments_count - 1 WHERE id = OLD.topic_id;
        END IF;
    END IF;
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_topic_comments_count
AFTER INSERT OR UPDATE OF deleted OR DELETE ON comments
FOR EACH ROW EXECUTE FUNCTION update_topic_comments_count();
