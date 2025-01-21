package act.ac.fhcampuswien.weather_app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Represents weather data and provides functionality to parse forecast data.
 */
public class WeatherData {
    private int temperature; // Temperature in Celsius
    private int humidity; // Humidity percentage
    private String description; // Weather description (e.g., clear sky, rain)
    private String cityName; // Name of the city
    private String countryCode; // ISO country code

    /**
     * Constructs a WeatherData object with the specified parameters.
     *
     * @param temperature The temperature in Celsius.
     * @param humidity The humidity percentage.
     * @param description A description of the weather.
     * @param cityName The name of the city.
     * @param countryCode The country code (ISO format).
     */
    public WeatherData(int temperature, int humidity, String description, String cityName, String countryCode) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    /**
     * Parses the JSON response to extract forecast data.
     *
     * @param jsonResponse The JSON response containing forecast data.
     * @return An array of WeatherData objects with the forecast information.
     */
    public WeatherData[] parseForecastData(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        // Extract city name and country code from the JSON response
        String cityName = jsonObject.get("city").getAsJsonObject().get("name").getAsString();
        String countryCode = jsonObject.get("city").getAsJsonObject().get("country").getAsString();

        // Extract the forecast list
        JsonArray list = jsonObject.getAsJsonArray("list");
        WeatherData[] forecastData = new WeatherData[list.size()];

        // Iterate through the forecast entries
        for (int i = 0; i < list.size(); i++) {
            JsonObject entry = list.get(i).getAsJsonObject();

            // Extract temperature, humidity, and description from each entry
            int temperature = entry.getAsJsonObject("main").get("temp").getAsInt();
            int humidity = entry.getAsJsonObject("main").get("humidity").getAsInt();
            String description = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Create a new WeatherData object for each forecast entry
            forecastData[i] = new WeatherData(temperature, humidity, description, cityName, countryCode);
        }

        return forecastData; // Return the array of WeatherData objects
    }

    /**
     * Retrieves the temperature.
     *
     * @return The temperature in Celsius.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Retrieves the humidity.
     *
     * @return The humidity percentage.
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * Retrieves the weather description.
     *
     * @return A string describing the weather.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the city name.
     *
     * @return The name of the city.
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Retrieves the country code.
     *
     * @return The country code in ISO format.
     */
    public String getCountryCode() {
        return countryCode;
    }
}
