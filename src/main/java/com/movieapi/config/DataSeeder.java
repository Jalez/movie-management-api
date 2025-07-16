package com.movieapi.config;

import com.movieapi.entity.Movie;
import com.movieapi.entity.Review;
import com.movieapi.repository.MovieRepository;
import com.movieapi.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "app.seeding.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        return args -> {
            // Check if database is already seeded
            long movieCount = movieRepository.count();
            if (movieCount > 0) {
                System.out.println("✅ Database already contains " + movieCount + " movies. Skipping seeding.");
                return;
            }

            System.out.println("🌱 Database is empty. Starting seeding process...");

            // Create movies
            List<Movie> movies = Arrays.asList(
                createMovie("Inception", "Sci-Fi", 2010, "Christopher Nolan"),
                createMovie("The Dark Knight", "Action", 2008, "Christopher Nolan"),
                createMovie("Interstellar", "Sci-Fi", 2014, "Christopher Nolan"),
                createMovie("Pulp Fiction", "Crime", 1994, "Quentin Tarantino"),
                createMovie("The Matrix", "Sci-Fi", 1999, "Lana Wachowski, Lilly Wachowski"),
                createMovie("The Godfather", "Crime", 1972, "Francis Ford Coppola"),
                createMovie("Casablanca", "Romance", 1942, "Michael Curtiz"),
                createMovie("The Shining", "Horror", 1980, "Stanley Kubrick"),
                createMovie("Finding Nemo", "Animation", 2003, "Andrew Stanton"),
                createMovie("The Lion King", "Animation", 1994, "Roger Allers, Rob Minkoff"),
                createMovie("Citizen Kane", "Drama", 1941, "Orson Welles"),
                createMovie("2001: A Space Odyssey", "Sci-Fi", 1968, "Stanley Kubrick"),
                createMovie("Star Wars", "Sci-Fi", 1977, "George Lucas"),
                createMovie("E.T. the Extra-Terrestrial", "Sci-Fi", 1982, "Steven Spielberg"),
                createMovie("Jurassic Park", "Adventure", 1993, "Steven Spielberg"),
                createMovie("Avatar", "Sci-Fi", 2009, "James Cameron"),
                createMovie("Titanic", "Romance", 1997, "James Cameron"),
                createMovie("The Avengers", "Action", 2012, "Joss Whedon"),
                createMovie("Spider-Man: Into the Spider-Verse", "Animation", 2018, "Bob Persichetti, Peter Ramsey, Rodney Rothman"),
                createMovie("Parasite", "Thriller", 2019, "Bong Joon-ho"),
                createMovie("Dune", "Sci-Fi", 2021, "Denis Villeneuve"),
                createMovie("Everything Everywhere All at Once", "Comedy", 2022, "Daniels"),
                createMovie("Top Gun: Maverick", "Action", 2022, "Joseph Kosinski"),
                createMovie("Oppenheimer", "Biography", 2023, "Christopher Nolan"),
                createMovie("Barbie", "Comedy", 2023, "Greta Gerwig")
            );

            // Save movies
            List<Movie> savedMovies = movieRepository.saveAll(movies);

            // Create reviews for some movies
            List<Review> reviews = Arrays.asList(
                // Inception reviews
                createReview(savedMovies.get(0), "DreamExplorer", "Mind-bending concept with incredible visual effects.", 8.9),
                createReview(savedMovies.get(0), "SciFiEnthusiast", "The concept of dream infiltration is fascinating.", 8.7),
                createReview(savedMovies.get(0), "FilmStudent", "Complex narrative structure that rewards multiple viewings.", 8.8),

                // The Dark Knight reviews
                createReview(savedMovies.get(1), "John Doe", "Amazing performance by Heath Ledger as the Joker.", 9.5),
                createReview(savedMovies.get(1), "Jane Smith", "Christopher Nolan at his best.", 9.0),
                createReview(savedMovies.get(1), "MovieFan123", "A masterpiece of the genre.", 9.8),

                // Interstellar reviews
                createReview(savedMovies.get(2), "SpaceEnthusiast", "Mind-bending sci-fi with incredible visuals.", 8.8),
                createReview(savedMovies.get(2), "SciFiLover", "The science is fascinating and the story is compelling.", 8.5),

                // Pulp Fiction reviews
                createReview(savedMovies.get(3), "TarantinoFan", "Revolutionary storytelling with unforgettable characters.", 9.2),
                createReview(savedMovies.get(3), "FilmCritic", "The non-linear narrative and sharp dialogue make this influential.", 9.0),
                createReview(savedMovies.get(3), "MovieBuff", "Quentin Tarantino's masterpiece.", 8.9),

                // The Matrix reviews
                createReview(savedMovies.get(4), "TechGuru", "Revolutionary special effects and a mind-bending story.", 8.7),
                createReview(savedMovies.get(4), "PhilosophyStudent", "The philosophical themes combined with groundbreaking action.", 8.5),

                // The Godfather reviews
                createReview(savedMovies.get(5), "ClassicFilmLover", "The perfect crime drama.", 9.5),
                createReview(savedMovies.get(5), "CinemaHistorian", "A masterpiece of American cinema.", 9.3),
                createReview(savedMovies.get(5), "MovieEnthusiast", "One of the greatest films ever made.", 9.4),

                // Casablanca reviews
                createReview(savedMovies.get(6), "ClassicLover", "Timeless romance with unforgettable dialogue.", 8.6),
                createReview(savedMovies.get(6), "FilmHistorian", "A perfect example of Golden Age Hollywood.", 8.4),

                // The Shining reviews
                createReview(savedMovies.get(7), "HorrorFan", "Kubrick's masterpiece of psychological horror.", 8.5),
                createReview(savedMovies.get(7), "CinemaBuff", "The atmosphere and tension build perfectly.", 8.3),

                // Finding Nemo reviews
                createReview(savedMovies.get(8), "AnimationLover", "Heartwarming story with beautiful animation.", 8.3),
                createReview(savedMovies.get(8), "FamilyViewer", "Pixar at their best.", 8.1),

                // The Lion King reviews
                createReview(savedMovies.get(9), "DisneyFan", "The circle of life! Beautiful animation and unforgettable songs.", 8.6),
                createReview(savedMovies.get(9), "MusicalLover", "The soundtrack is incredible and the story is timeless.", 8.4),

                // Citizen Kane reviews
                createReview(savedMovies.get(10), "FilmCritic", "Revolutionary cinematography and storytelling.", 8.4),
                createReview(savedMovies.get(10), "CinemaScholar", "The technical innovations alone make this a masterpiece.", 8.2),

                // Star Wars reviews
                createReview(savedMovies.get(12), "StarWarsFan", "A long time ago in a galaxy far, far away...", 8.7),
                createReview(savedMovies.get(12), "SciFiLover", "Revolutionary special effects and a timeless story.", 8.5),

                // Avatar reviews
                createReview(savedMovies.get(15), "VisualEffectsFan", "Groundbreaking 3D technology and stunning visuals.", 7.9),
                createReview(savedMovies.get(15), "SciFiViewer", "The story is familiar but the visual experience is revolutionary.", 7.7),

                // Titanic reviews
                createReview(savedMovies.get(16), "RomanceLover", "Epic love story with spectacular disaster sequences.", 7.9),
                createReview(savedMovies.get(16), "HistoricalDramaFan", "The historical accuracy combined with the romance makes this unforgettable.", 7.7),

                // The Avengers reviews
                createReview(savedMovies.get(17), "MarvelFan", "The first time we see all these heroes together!", 8.1),
                createReview(savedMovies.get(17), "SuperheroLover", "Perfect balance of action, humor, and character development.", 7.9),

                // Spider-Man: Into the Spider-Verse reviews
                createReview(savedMovies.get(18), "AnimationEnthusiast", "Revolutionary animation style that perfectly captures the comic book aesthetic.", 8.5),
                createReview(savedMovies.get(18), "SpiderManFan", "Fresh take on Spider-Man with incredible visuals and heart.", 8.3),

                // Parasite reviews
                createReview(savedMovies.get(19), "InternationalFilmFan", "Brilliant social commentary wrapped in a thrilling story.", 8.7),
                createReview(savedMovies.get(19), "ThrillerLover", "The genre shifts are masterful and the commentary on class is razor-sharp.", 8.5),

                // Dune reviews
                createReview(savedMovies.get(20), "SciFiReader", "Villeneuve perfectly captures the epic scale of Herbert's novel.", 8.1),
                createReview(savedMovies.get(20), "EpicFilmFan", "Spectacular visuals and faithful adaptation.", 7.9),

                // Everything Everywhere All at Once reviews
                createReview(savedMovies.get(21), "MultiverseExplorer", "Mind-bending multiverse story with heart.", 7.9),
                createReview(savedMovies.get(21), "IndieFilmLover", "Creative, emotional, and completely original.", 7.7),

                // Top Gun: Maverick reviews
                createReview(savedMovies.get(22), "ActionFan", "Spectacular aerial sequences and a worthy sequel to the original.", 8.4),
                createReview(savedMovies.get(22), "SequelLover", "Rare sequel that improves on the original.", 8.2),

                // Oppenheimer reviews
                createReview(savedMovies.get(23), "BiographyFan", "Nolan's most mature film. Cillian Murphy delivers a career-defining performance.", 8.5),
                createReview(savedMovies.get(23), "HistoricalDramaLover", "The tension builds perfectly as we approach the Trinity test.", 8.3),

                // Barbie reviews
                createReview(savedMovies.get(24), "ComedyLover", "Surprisingly deep commentary wrapped in a fun, colorful package.", 7.0),
                createReview(savedMovies.get(24), "SocialCommentaryFan", "Gerwig tackles big themes with humor and heart.", 6.8)
            );

            // Save reviews
            reviewRepository.saveAll(reviews);

            // Calculate and update movie ratings
            for (Movie movie : savedMovies) {
                List<Review> movieReviews = reviewRepository.findByMovieId(movie.getId());
                if (!movieReviews.isEmpty()) {
                    double averageRating = movieReviews.stream()
                            .mapToDouble(Review::getRating)
                            .average()
                            .orElse(0.0);
                    movie.setRating(BigDecimal.valueOf(Math.round(averageRating * 10.0) / 10.0));
                    movieRepository.save(movie);
                }
            }

            System.out.println("✅ Database seeded with " + savedMovies.size() + " movies and " + reviews.size() + " reviews!");
        };
    }

    private Movie createMovie(String title, String genre, int releaseYear, String director) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseYear(releaseYear);
        movie.setDirector(director);
        movie.setRating(null); // Will be calculated from reviews
        return movie;
    }

    private Review createReview(Movie movie, String userName, String reviewText, double rating) {
        Review review = new Review();
        review.setMovie(movie);
        review.setUserName(userName);
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        return review;
    }
} 