package act.ac.fhcampuswien.weather_app;

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