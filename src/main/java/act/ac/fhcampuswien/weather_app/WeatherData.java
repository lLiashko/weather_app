package act.ac.fhcampuswien.weather_app;

public class WeatherData {
    int temperature;
    int humidity;
    String description;
    String cityName;
    String countryCode;

    public WeatherData(int temperature, int humidity, String description, String cityName, String countryCode) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    public double getTemperature() {
        return temperature;
    }
    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
