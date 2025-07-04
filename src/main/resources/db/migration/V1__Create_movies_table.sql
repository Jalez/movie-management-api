-- Initial database schema for Movie Management API
-- Creates the movies table with all required fields

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    release_year INTEGER NOT NULL,
    director VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for common search queries
CREATE INDEX idx_movies_genre ON movies(genre);
CREATE INDEX idx_movies_release_year ON movies(release_year);
CREATE INDEX idx_movies_rating ON movies(rating);
CREATE INDEX idx_movies_director ON movies(director);

-- Insert some sample data
INSERT INTO movies (title, genre, release_year, director, rating) VALUES
('Inception', 'Sci-Fi', 2010, 'Christopher Nolan', 9),
('Interstellar', 'Sci-Fi', 2014, 'Christopher Nolan', 8),
('The Dark Knight', 'Action', 2008, 'Christopher Nolan', 9),
('Pulp Fiction', 'Crime', 1994, 'Quentin Tarantino', 9),
('The Matrix', 'Sci-Fi', 1999, 'Lana Wachowski, Lilly Wachowski', 8);
