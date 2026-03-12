-- Author interactions table (notification system)
CREATE TABLE author_interactions (
    id UUID PRIMARY KEY,
    recipient_author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    actor_author_id UUID NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    interaction_type VARCHAR(30) NOT NULL,
    target_type VARCHAR(15) NOT NULL,
    target_id UUID NOT NULL,
    target_title VARCHAR(255),
    target_slug VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

-- Main index: fetch interactions ordered by date
CREATE INDEX idx_interactions_recipient_created ON author_interactions(recipient_author_id, created_at DESC);

-- Partial index for unread count (very efficient)
CREATE INDEX idx_interactions_unread ON author_interactions(recipient_author_id) WHERE is_read = FALSE;

-- Unique index to prevent duplicates (same actor + type + target)
CREATE UNIQUE INDEX idx_interactions_unique ON author_interactions(actor_author_id, interaction_type, target_id);

-- Index for cleanup when content is deleted
CREATE INDEX idx_interactions_target ON author_interactions(target_type, target_id);

-- Trigger to delete interactions when an article is deleted
CREATE OR REPLACE FUNCTION delete_article_interactions()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM author_interactions WHERE target_type = 'ARTICLE' AND target_id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_delete_article_interactions
BEFORE DELETE ON articles
FOR EACH ROW EXECUTE FUNCTION delete_article_interactions();

-- Trigger to delete interactions when a topic is deleted
CREATE OR REPLACE FUNCTION delete_topic_interactions()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM author_interactions WHERE target_type = 'TOPIC' AND target_id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_delete_topic_interactions
BEFORE DELETE ON topics
FOR EACH ROW EXECUTE FUNCTION delete_topic_interactions();

-- Trigger to delete interactions when a comment is deleted (hard delete)
CREATE OR REPLACE FUNCTION delete_comment_interactions()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM author_interactions WHERE target_type = 'COMMENT' AND target_id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_delete_comment_interactions
BEFORE DELETE ON comments
FOR EACH ROW EXECUTE FUNCTION delete_comment_interactions();
