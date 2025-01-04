package act.ac.fhcampuswien.weather_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    private Label humidityLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    protected void onGetWeatherButtonClick() {
        String cityName = cityTextField.getText();
        String countryCode = countryTextField.getText();
        WeatherAPI api = new WeatherAPI();

        try {
            String jsonResponse = api.fetchWeather(cityName, countryCode);
            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            cityLabel.setText("City: " + weatherData.getCityName());
            temperatureLabel.setText("Temperature: " + weatherData.getTemperature() + "°C");
            humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%");
            descriptionLabel.setText("Description: " + weatherData.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            cityLabel.setText("Error fetching data for city: " + cityName);
        }
    }

    public void updateWeatherUI(WeatherData weatherData) {
        cityLabel.setText("City: " + weatherData.getCityName());
        temperatureLabel.setText("Temperature: " + (int) weatherData.getTemperature() + "°C");
        humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%");
        descriptionLabel.setText("Description: " + weatherData.getDescription());
    }
}