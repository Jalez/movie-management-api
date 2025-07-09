-- Add comprehensive sample movie data for testing
-- This migration adds diverse movies for thorough API testing

-- Clear existing sample data to avoid conflicts
DELETE FROM movies WHERE id IN (1, 2, 3, 4, 5);

-- Reset sequence
ALTER SEQUENCE movies_id_seq RESTART WITH 1;

-- Insert comprehensive test data covering various scenarios
INSERT INTO movies (title, genre, release_year, director, rating) VALUES
-- Popular blockbusters from different decades
('Inception', 'Sci-Fi', 2010, 'Christopher Nolan', 8.8),
('The Dark Knight', 'Action', 2008, 'Christopher Nolan', 9.0),
('Interstellar', 'Sci-Fi', 2014, 'Christopher Nolan', 8.6),
('Pulp Fiction', 'Crime', 1994, 'Quentin Tarantino', 8.9),
('The Matrix', 'Sci-Fi', 1999, 'Lana Wachowski, Lilly Wachowski', 8.7),

-- Different genres
('The Godfather', 'Crime', 1972, 'Francis Ford Coppola', 9.2),
('Casablanca', 'Romance', 1942, 'Michael Curtiz', 8.5),
('The Shining', 'Horror', 1980, 'Stanley Kubrick', 8.4),
('Finding Nemo', 'Animation', 2003, 'Andrew Stanton', 8.2),
('The Lion King', 'Animation', 1994, 'Roger Allers, Rob Minkoff', 8.5),

-- Various decades representation
('Citizen Kane', 'Drama', 1941, 'Orson Welles', 8.3),
('2001: A Space Odyssey', 'Sci-Fi', 1968, 'Stanley Kubrick', 8.3),
('Star Wars', 'Sci-Fi', 1977, 'George Lucas', 8.6),
('E.T. the Extra-Terrestrial', 'Sci-Fi', 1982, 'Steven Spielberg', 7.9),
('Jurassic Park', 'Adventure', 1993, 'Steven Spielberg', 8.1),

-- Different rating ranges
('Avatar', 'Sci-Fi', 2009, 'James Cameron', 7.8),
('Titanic', 'Romance', 1997, 'James Cameron', 7.8),
('The Avengers', 'Action', 2012, 'Joss Whedon', 8.0),
('Spider-Man: Into the Spider-Verse', 'Animation', 2018, 'Bob Persichetti, Peter Ramsey, Rodney Rothman', 8.4),
('Parasite', 'Thriller', 2019, 'Bong Joon-ho', 8.6),

-- Edge cases for testing
('A Very Long Movie Title That Tests the Maximum Length Constraints of the Database Field', 'Drama', 2020, 'Test Director', 7.5),
('Movie with Special Characters: åéîøü & Symbols!@#$%', 'Comedy', 2021, 'Director with Ümlauts', 6.8),
('', 'Mystery', 2022, 'Anonymous', 5.0), -- This should fail validation
('Normal Movie', '', 2023, 'John Doe', 8.0), -- This should fail validation

-- Movies from current decade
('Dune', 'Sci-Fi', 2021, 'Denis Villeneuve', 8.0),
('Everything Everywhere All at Once', 'Comedy', 2022, 'Daniels', 7.8),
('Top Gun: Maverick', 'Action', 2022, 'Joseph Kosinski', 8.3),
('Oppenheimer', 'Biography', 2023, 'Christopher Nolan', 8.4),
('Barbie', 'Comedy', 2023, 'Greta Gerwig', 6.9),

-- Foreign films
('Seven Samurai', 'Action', 1954, 'Akira Kurosawa', 8.6),
('8½', 'Drama', 1963, 'Federico Fellini', 8.0),
('Bicycle Thieves', 'Drama', 1948, 'Vittorio De Sica', 8.3),

-- Different directors with multiple films
('Kill Bill: Vol. 1', 'Action', 2003, 'Quentin Tarantino', 8.1),
('Django Unchained', 'Western', 2012, 'Quentin Tarantino', 8.4),
('The Departed', 'Crime', 2006, 'Martin Scorsese', 8.5),
('Goodfellas', 'Crime', 1990, 'Martin Scorsese', 8.7),

-- Extreme ratings for boundary testing
('Perfect Movie', 'Fantasy', 2024, 'Ideal Director', 10.0),
('Terrible Movie', 'Horror', 2024, 'Bad Director', 0.0),

-- Future release year (should be allowed but unusual)
('Future Film', 'Sci-Fi', 2025, 'Time Traveler', 7.0);

-- Remove the invalid entries that would fail validation
DELETE FROM movies WHERE title = '' OR genre = '';

-- Add some indexes for performance testing with larger dataset
-- (These already exist from V1 migration, but listing here for documentation)
-- CREATE INDEX IF NOT EXISTS idx_movies_genre ON movies(genre);
-- CREATE INDEX IF NOT EXISTS idx_movies_release_year ON movies(release_year);
-- CREATE INDEX IF NOT EXISTS idx_movies_rating ON movies(rating);
-- CREATE INDEX IF NOT EXISTS idx_movies_director ON movies(director);

-- Add composite indexes for common search combinations
CREATE INDEX IF NOT EXISTS idx_movies_genre_year ON movies(genre, release_year);
CREATE INDEX IF NOT EXISTS idx_movies_rating_genre ON movies(rating, genre);
CREATE INDEX IF NOT EXISTS idx_movies_director_rating ON movies(director, rating);
