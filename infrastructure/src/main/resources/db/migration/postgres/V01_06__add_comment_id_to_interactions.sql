-- Add comment_id column for comment-related interactions
ALTER TABLE author_interactions ADD COLUMN comment_id UUID;

-- Index for cleanup when comment is deleted
CREATE INDEX idx_interactions_comment ON author_interactions(comment_id) WHERE comment_id IS NOT NULL;
