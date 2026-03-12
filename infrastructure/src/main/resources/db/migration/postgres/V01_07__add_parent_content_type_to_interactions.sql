-- Add parent_content_type for comment interactions (ARTICLE or TOPIC)
ALTER TABLE author_interactions ADD COLUMN parent_content_type VARCHAR(15);
