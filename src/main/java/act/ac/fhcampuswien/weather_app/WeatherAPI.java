package act.ac.fhcampuswien.weather_app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WeatherAPI {
    private static final String API_KEY = "23fa24bcbc65e5e9312f629d89ce1cc1";
    private static final String BASE_URL = "https://pro.openweathermap.org/data/2.5/weather";

    /**
     * Fetches weather data for a given city.
     *
     * @param cityName The name of the city.
     * @param countryCode The name of the country.
     * @return JSON response as a string.
     * @throws Exception If there's an error during the API request.
     */
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

    public WeatherData parseWeatherData(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        int temperature = jsonObject.getAsJsonObject("main").get("temp").getAsInt();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        String description = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
        String cityName = jsonObject.get("name").getAsString();
        String countryCode = jsonObject.getAsJsonObject("sys").get("country").getAsString();

        return new WeatherData(temperature, humidity, description, cityName, countryCode);
    }
}
