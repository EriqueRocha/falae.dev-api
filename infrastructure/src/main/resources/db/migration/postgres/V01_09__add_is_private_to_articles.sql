-- Add is_private column to articles table
ALTER TABLE articles ADD COLUMN is_private BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index for filtering private articles
CREATE INDEX idx_articles_is_private ON articles(is_private);

-- Create composite index for author + is_private queries
CREATE INDEX idx_articles_author_is_private ON articles(author_id, is_private);
