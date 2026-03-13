-- Add parent_author_user_name for comment interactions (username of the article/topic owner)
ALTER TABLE author_interactions ADD COLUMN parent_author_user_name VARCHAR(50);
