package act.ac.fhcampuswien.weather_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HelloController {
    @FXML
    private TextField cityTextField;

    @FXML
    private TextField countryTextField;

    @FXML
    private Label cityLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label moodLabel;

    @FXML
    private Label suggestionLabel;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private VBox rootVBox; // Root VBox to change background color

    public void onGetWeatherButtonClick() {
        String cityName = cityTextField.getText().trim();
        String countryCode = countryTextField.getText().trim();

        if (cityName.isEmpty() || countryCode.isEmpty()) {
            cityLabel.setText("Please enter a city and country code!");
            return;
        }

        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchWeather(cityName, countryCode);
            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            cityLabel.setText(weatherData.getCityName());
            temperatureLabel.setText(weatherData.getTemperature() + "°C");
            descriptionLabel.setText(weatherData.getDescription());
            humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%");

            updateWeatherIcon(weatherData.getDescription());
            assignMoodAndSuggestion(weatherData.getDescription());
            updateBackgroundColor(weatherData.getDescription());
        } catch (Exception e) {
            cityLabel.setText("Error: Could not fetch data for " + cityName);
            e.printStackTrace();
        }
    }

    private void updateWeatherIcon(String weatherDescription) {
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
            iconPath = "/icons/default.png";
        }

        try {
            Image image = new Image(getClass().getResource(iconPath).toExternalForm());
            weatherIcon.setImage(image);
        } catch (Exception e) {
            System.out.println("Error loading icon for description: " + weatherDescription);
            e.printStackTrace();
        }
    }

    private void assignMoodAndSuggestion(String weatherDescription) {
        String mood;
        String suggestion;

        if (weatherDescription.toLowerCase().contains("clear")) {
            mood = "Sunny: Having Main Character Energy";
            suggestion = "Listen to 'Walking on Sunshine'!";
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            mood = "Cloudy: Feeling Pensive";
            suggestion = "Watch 'The Cloud Atlas'.";
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            mood = "Rainy: Feeling Emo";
            suggestion = "Listen to 'Raindrops Keep Fallin' on My Head'.";
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            mood = "Snowy: Feeling Cozy";
            suggestion = "Watch 'Frozen'.";
        } else {
            mood = "Unknown: Feeling Curious";
            suggestion = "Explore new music or movies!";
        }

        moodLabel.setText(mood);
        suggestionLabel.setText(suggestion);
    }

    private void updateBackgroundColor(String weatherDescription) {
        String backgroundColor;

        if (weatherDescription.toLowerCase().contains("clear")) {
            backgroundColor = "#FFD700"; // Darker but bright yellow (goldenrod)
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            backgroundColor = "#B0C4DE"; // Cloudy (light steel blue)
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            backgroundColor = "#4682B4"; // Rainy (steel blue)
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            backgroundColor = "#ADD8E6"; // Snowy (light blue)
        } else {
            backgroundColor = "#2c3e50"; // Default (dark slate gray)
        }

        rootVBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 20; -fx-font-family: 'Arial';");
    }
}