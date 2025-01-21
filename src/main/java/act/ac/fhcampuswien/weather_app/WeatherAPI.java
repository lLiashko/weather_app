package act.ac.fhcampuswien.weather_app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * WeatherAPI class provides methods to fetch and parse weather data
 * from the OpenWeatherMap API.
 */
public class WeatherAPI {
    private static final String API_KEY = "api"; // API key for authentication
    private static final String BASE_URL = "https://pro.openweathermap.org/data/2.5/weather"; // Base URL for weather data

    /**
     * Fetches forecast data for the specified city and country.
     *
     * @param cityName The name of the city.
     * @param countryCode The country code (ISO format).
     * @return JSON response as a string.
     * @throws Exception If an error occurs during the API request.
     */
    public String fetchWeather(String cityName, String countryCode) throws Exception {
        String urlString = BASE_URL + "?q=" + cityName + "," + countryCode + "&units=metric&APPID=" + API_KEY;
        URL url = new URL(urlString);

        // Open the connection and set request properties
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // Set connection timeout (5 seconds)
        connection.setReadTimeout(5000); // Set read timeout (5 seconds)

        // Check the response code from the API
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // If response is successful
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            // Read the response line by line
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString(); // Return the API response as a string
        } else {
            // Throw an exception if the response code indicates an error
            throw new Exception("Failed to fetch weather data. HTTP code: " + responseCode + ", error: " + connection.getResponseMessage());
        }
    }

    public WeatherData parseWeatherData(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        // Extract temperature, humidity, and weather description
        int temperature = jsonObject.getAsJsonObject("main").get("temp").getAsInt();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        String description = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
        String cityName = jsonObject.get("name").getAsString();
        String countryCode = jsonObject.getAsJsonObject("sys").get("country").getAsString();

        // Create and return a WeatherData object
        return new WeatherData(temperature, humidity, description, cityName, countryCode);
    }
}
