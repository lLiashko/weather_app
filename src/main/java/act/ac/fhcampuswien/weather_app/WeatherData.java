package act.ac.fhcampuswien.weather_app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WeatherData {
    int temperature;
    int humidity;
    String description;
    String cityName;
    String countryCode;

    public WeatherData[] parseForecastData(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        // Extrahiere cityName und countryCode aus der Antwort
        String cityName = jsonObject.get("city").getAsJsonObject().get("name").getAsString();
        String countryCode = jsonObject.get("city").getAsJsonObject().get("country").getAsString();

        // Wettervorhersage-Daten auslesen
        JsonArray list = jsonObject.getAsJsonArray("list");
        WeatherData[] forecastData = new WeatherData[list.size()];

        for (int i = 0; i < list.size(); i++) {
            JsonObject entry = list.get(i).getAsJsonObject();

            // Temperatur, Luftfeuchtigkeit, Beschreibung und Stadtname extrahieren
            int temperature = entry.getAsJsonObject("main").get("temp").getAsInt();
            int humidity = entry.getAsJsonObject("main").get("humidity").getAsInt();
            String description = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Erstellen des WeatherData-Objekts
            forecastData[i] = new WeatherData(temperature, humidity, description, cityName, countryCode);
        }

        return forecastData;
    }


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
