-- Update rating column to support decimal values (0.0-10.0)
-- This migration changes the rating from INTEGER to DECIMAL to support decimal ratings

-- First, drop the existing constraint
ALTER TABLE movies DROP CONSTRAINT IF EXISTS movies_rating_check;

-- Change the column type to DECIMAL
ALTER TABLE movies ALTER COLUMN rating TYPE DECIMAL(3,1);

-- Add new constraint for decimal range
ALTER TABLE movies ADD CONSTRAINT movies_rating_check CHECK (rating >= 0.0 AND rating <= 10.0);

-- Update existing sample data to use decimal values
UPDATE movies SET rating = 8.8 WHERE title = 'Inception';
UPDATE movies SET rating = 8.6 WHERE title = 'Interstellar';
UPDATE movies SET rating = 9.0 WHERE title = 'The Dark Knight';
UPDATE movies SET rating = 8.9 WHERE title = 'Pulp Fiction';
UPDATE movies SET rating = 8.7 WHERE title = 'The Matrix';
