package act.ac.fhcampuswien.weather_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HelloController {
    @FXML
    private TextField cityTextField; // Input for city name

    @FXML
    private TextField countryTextField; // Input for country code

    @FXML
    private Label cityLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label humidityLabel; // Label for humidity

    @FXML
    private ImageView weatherIcon;

    public void onGetWeatherButtonClick() {
        // Get the input values from the text fields
        String cityName = cityTextField.getText().trim();
        String countryCode = countryTextField.getText().trim();

        // Validate inputs
        if (cityName.isEmpty() || countryCode.isEmpty()) {
            cityLabel.setText("Please enter a city and country code!");
            return;
        }

        try {
            WeatherAPI api = new WeatherAPI();
            // Fetch weather data using the entered city and country
            String jsonResponse = api.fetchWeather(cityName, countryCode);
            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            // Update weather details
            cityLabel.setText(weatherData.getCityName());
            temperatureLabel.setText(weatherData.getTemperature() + "°C");
            descriptionLabel.setText(weatherData.getDescription());
            humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%"); // Update humidity

            // Update weather icon (if applicable)
            updateWeatherIcon(weatherData.getDescription());
        } catch (Exception e) {
            cityLabel.setText("Error: Could not fetch data for " + cityName);
            e.printStackTrace();
        }
    }

    private void updateWeatherIcon(String weatherDescription) {
        // Handle weather icons based on description
        String iconPath;

        if (weatherDescription.toLowerCase().contains("clear")) {
            iconPath = "/icons/clear.png";
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            iconPath = "/icons/cloudy.png";
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            iconPath = "/icons/rain.png";
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            iconPath = "/icons/snow.png";
        } else {
            iconPath = "/icons/default.png"; // Default icon
        }

        try {
            Image image = new Image(getClass().getResource(iconPath).toExternalForm());
            weatherIcon.setImage(image);
        } catch (Exception e) {
            System.out.println("Error loading icon for description: " + weatherDescription);
            e.printStackTrace();
        }
    }
}