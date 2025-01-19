package act.ac.fhcampuswien.weather_app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class WeatherAPI {
    private static final String API_KEY = "";
    private static final String BASE_URL = "https://pro.openweathermap.org/data/2.5/weather";

    /**
     * Fetches weather data for a given city.
     *
     * @param cityName The name of the city.
     * @param countryCode The name of the country.
     * @return JSON response as a string.
     * @throws Exception If there's an error during the API request.
     */
    public String fetchForecast(String cityName, String countryCode) throws Exception {
        String urlString = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s,%s&units=metric&cnt=5&appid=%s",
                cityName, countryCode, API_KEY);
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString(); // Rückgabe der API-Antwort (JSON)
        } else {
            throw new Exception("Failed to fetch weather forecast. HTTP code: " + responseCode + ", error: " + connection.getResponseMessage());
        }
    }

    public String fetchWeather(String cityName, String countryCode) throws Exception {
        String urlString = BASE_URL + "?q=" + cityName + "," + countryCode + "&units=metric&APPID=" + API_KEY;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new Exception("Failed to fetch weather data. HTTP code: " + responseCode + ", error: " + connection.getResponseMessage());
        }
    }

    /**
     * Parses the JSON response for the forecast data (5 days).
     *
     * @param jsonResponse The JSON response from the API.
     * @return An array of WeatherData containing the forecast information.
     */
    public WeatherData[] parseForecastData(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        // Extract the city name and country code
        String cityName = jsonObject.getAsJsonObject("city").get("name").getAsString();
        String countryCode = jsonObject.getAsJsonObject("city").get("country").getAsString();

        // Extract forecast data
        JsonArray list = jsonObject.getAsJsonArray("list");
        WeatherData[] forecastData = new WeatherData[list.size()];

        for (int i = 0; i < list.size(); i++) {
            JsonObject entry = list.get(i).getAsJsonObject();

            // Extract temperature, humidity, description, and other data
            int temperature = entry.getAsJsonObject("main").get("temp").getAsInt();
            int humidity = entry.getAsJsonObject("main").get("humidity").getAsInt();
            String description = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Create the WeatherData object
            forecastData[i] = new WeatherData(temperature, humidity, description, cityName, countryCode);
        }

        return forecastData;
    }

    /**
     * Parses the current weather data (single-day data).
     *
     * @param jsonResponse The JSON response from the API.
     * @return WeatherData object containing the weather information.
     */
    public WeatherData parseWeatherData(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        int temperature = jsonObject.getAsJsonObject("main").get("temp").getAsInt();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        String description = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
        String cityName = jsonObject.get("name").getAsString();
        String countryCode = jsonObject.getAsJsonObject("sys").get("country").getAsString();

        return new WeatherData(temperature, humidity, description, cityName, countryCode);
    }
}
