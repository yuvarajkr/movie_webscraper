package com.movie.webscraper.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Controller
public class MovieController {

    // The TMDb API key is stored in the application.properties file
    @Value("${tmdb.api.key}")
    private String apiKey;

    @GetMapping("/")
    public String homePage() {
        return "index"; // Return the index.html page
    }

    @PostMapping("/scrap")
    public String getMovies(@RequestParam("content") String content, Model model) {
        String searchString = content.replace(" ", "%20");
        String tmdbUrl = "https://api.themoviedb.org/3/search/movie";

        // Create a RestTemplate instance to make HTTP requests
        RestTemplate restTemplate = new RestTemplate();

        // Build the URI with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tmdbUrl)
                .queryParam("api_key", apiKey)
                .queryParam("query", searchString);

        // Make the API request and parse the response
        Map<String, Object> response = restTemplate.getForObject(builder.toUriString(), Map.class);

        if (response != null && response.containsKey("results")) {
            // Extract the list of movies from the response
            List<Map<String, Object>> movies = (List<Map<String, Object>>) response.get("results");

            if (movies.isEmpty()) {
                model.addAttribute("error", "No movies found matching the search term.");
                return "index";
            }

            // Add movies to the model to display on the results page
            model.addAttribute("movies", movies);
            return "results"; // Return the results.html page to show the movies
        } else {
            model.addAttribute("error", "Something went wrong while fetching movie data.");
            return "index";
        }
    }
}
