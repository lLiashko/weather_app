package act.ac.fhcampuswien.weather_app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;

public class HelloController {

    @FXML
    private TextField cityTextField;
    @FXML
    private ListView<String> listView;
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
    @FXML
    private Button goBackButton;
    @FXML
    private Button getWeatherButton;
    @FXML
    private Button saveSettingsButton;
    @FXML
    private RadioButton celsiusRadioButton;
    @FXML
    private RadioButton fahrenheitRadioButton;
    @FXML
    private RadioButton englishRadioButton;
    @FXML
    private RadioButton germanRadioButton;
    @FXML
    private Tab settingsTab;
    @FXML
    private Label settingsTitleLabel;
    @FXML
    private Label temperatureUnitLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private GridPane forecastGrid;
    @FXML
    private Button getForecastButton;
    @FXML
    private TextField cityInputField; // TextField für Stadt

    @FXML
    private TextField countryCodeInputField; // TextField für Ländercode


    private boolean isCelsius = true; // Default to Celsius
    private double currentTemperatureInCelsius; // Store the current temperature in Celsius
    private String currentLanguage = "English"; // Default to English
    private ObservableList<String> cityList = FXCollections.observableArrayList();
    private String selectedCity = "";


    @FXML
    public void onGetForecastButtonClick() {
        // Use the ListView selection, just like in onGetWeatherButtonClick
        if (this.selectedCity == null || this.selectedCity.isEmpty()) {
            return;
        }
        String[] cityAndCountryCode = this.selectedCity.split(";");
        if (cityAndCountryCode.length < 2) {
            return;
        }

        String cityName = cityAndCountryCode[0];
        String countryCode = cityAndCountryCode[1];

        // Now fetch the forecast using the REAL city & country:
        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchForecast(cityName, countryCode);

            WeatherData[] forecastData = api.parseForecastData(jsonResponse);

            clearForecastGrid();
            forecastGrid.getChildren().clear();

            // Fill the grid with your forecast data
            for (int i = 0; i < forecastData.length; i++) {
                WeatherData data = forecastData[i];
                double temperature = isCelsius
                        ? data.getTemperature()
                        : (data.getTemperature() * 9.0 / 5.0) + 32.0;

                Label dayLabel = new Label(translateDay(i));
                Label tempLabel = new Label(String.format("%.1f", temperature)
                        + (isCelsius ? "°C" : "°F"));
                Label descLabel = new Label(translateWeatherDescription(data.getDescription()));
                Label humidityLabel = new Label(translateHumidity(data.getHumidity()));

                forecastGrid.add(dayLabel,     0, i);
                forecastGrid.add(tempLabel,    1, i);
                forecastGrid.add(descLabel,    2, i);
                forecastGrid.add(humidityLabel,3, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForecastGrid() {
        forecastGrid.getChildren().clear();
    }

    @FXML
    public void initialize() {
        clearWeatherData();
        forecastGrid.getChildren().clear();
        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);
        getForecastButton.setVisible(true);

        applyButtonStyle(getWeatherButton);
        applyButtonStyle(goBackButton);

        ToggleGroup temperatureToggleGroup = new ToggleGroup();
        celsiusRadioButton.setToggleGroup(temperatureToggleGroup);
        fahrenheitRadioButton.setToggleGroup(temperatureToggleGroup);
        celsiusRadioButton.setSelected(true);

        ToggleGroup languageToggleGroup = new ToggleGroup();
        englishRadioButton.setToggleGroup(languageToggleGroup);
        germanRadioButton.setToggleGroup(languageToggleGroup);
        englishRadioButton.setSelected(true);

        celsiusRadioButton.setOnAction(event -> updateTemperatureUnit(true));
        fahrenheitRadioButton.setOnAction(event -> updateTemperatureUnit(false));
        englishRadioButton.setOnAction(event -> updateLanguage("English"));
        germanRadioButton.setOnAction(event -> updateLanguage("German"));
        loadCSVFiles();

        FilteredList<String> filteredList = new FilteredList<>(cityList, s -> true);
        listView.setItems(filteredList);

        cityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(city -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return city.toLowerCase().contains(newValue.toLowerCase());
            });
        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, userSelection) -> {
            if (userSelection != null) {
                this.selectedCity = userSelection;
            }
        });
    }

    private void loadCSVFiles() {
        try (InputStream input = getClass().getResourceAsStream("/data/country_codes.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.cityList.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #4682B4; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 5px;");

        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #5A9BD4; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 5px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #4682B4; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 5px;"));
    }

    @FXML
    public void onGetWeatherButtonClick() {
        if (this.selectedCity.isEmpty()) {
            return;
        }
        String[] cityAndCountryCode = this.selectedCity.split(";");
        String cityName = cityAndCountryCode[0];
        String countryCode = cityAndCountryCode[1];

        clearWeatherData();

        if (cityName.isEmpty() || countryCode.isEmpty()) {
            cityLabel.setText(currentLanguage.equals("English") ? "Please enter a city and country code!" : "Bitte gib eine Stadt und einen Ländercode ein!");
            return;
        }

        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchWeather(cityName, countryCode);

            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            cityLabel.setText(weatherData.getCityName());
            currentTemperatureInCelsius = weatherData.getTemperature();
            updateTemperatureLabel(currentTemperatureInCelsius);
            descriptionLabel.setText(translateWeatherDescription(weatherData.getDescription()));
            humidityLabel.setText(currentLanguage.equals("English") ? "Humidity: " + weatherData.getHumidity() + "%" : "Luftfeuchtigkeit: " + weatherData.getHumidity() + "%");

            updateWeatherIcon(weatherData.getDescription());
            assignMoodAndSuggestion(weatherData.getDescription());
            updateBackgroundColor(weatherData.getDescription());

            getForecastButton.setVisible(false);

            goBackButton.setVisible(true);
            getWeatherButton.setVisible(false);
            cityTextField.setVisible(false);
            listView.setVisible(false);

        } catch (Exception e) {
            cityLabel.setText(currentLanguage.equals("English") ? "Error: Could not fetch data for " + cityName : "Fehler: Daten für " + cityName + " konnten nicht abgerufen werden");
            e.printStackTrace();
        }
    }

    @FXML
    public void onGoBackButtonClick() {
        clearWeatherData();
        forecastGrid.getChildren().clear();

        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);
        getForecastButton.setVisible(true);
        cityTextField.setVisible(true);
        listView.setVisible(true);

        celsiusRadioButton.setSelected(true);
        updateTemperatureUnit(true);
    }

    private void clearWeatherData() {
        cityLabel.setText("");
        temperatureLabel.setText("");
        descriptionLabel.setText("");
        humidityLabel.setText("");
        moodLabel.setText("");
        suggestionLabel.setText("");

        weatherIcon.setImage(null);
        rootVBox.setStyle("");
        currentTemperatureInCelsius = 0.0;
    }

    private void updateWeatherIcon(String weatherDescription) {
        String iconFileName;

        switch (weatherDescription.toLowerCase()) {
            case "clear sky":
                iconFileName = "clear.png";
                break;
            case "few clouds":
                iconFileName = "cloudy.png";
                break;
            case "scattered clouds":
                iconFileName = "cloudy.png";
                break;
            case "broken clouds":
                iconFileName = "cloudy.png";
                break;
            case "shower rain":
                iconFileName = "rain.png";
                break;
            case "rain":
            case "light intensity drizzle":
                iconFileName = "rain.png";
                break;
            case "overcast clouds":
                iconFileName = "cloudy.png";
                break;
            case "thunderstorm":
                iconFileName = "cloudy.png";
                break;
            case "snow":
                iconFileName = "snow.png";
                break;
            case "mist":
                iconFileName = "humidity.png";
                break;
            default:
                iconFileName = "default.png";
        }

        try {
            String iconPath = "/icons/" + iconFileName;
            Image icon = new Image(getClass().getResource(iconPath).toExternalForm());
            weatherIcon.setImage(icon);
        } catch (NullPointerException e) {
            System.err.println("Error loading icon for description: " + weatherDescription + ". File not found: " + iconFileName);
        } catch (Exception e) {
            System.err.println("Error loading icon for description: " + weatherDescription + ". " + e.getMessage());
        }
    }

    private void assignMoodAndSuggestion(String weatherDescription) {
        String mood;
        String suggestion;

        if (weatherDescription.toLowerCase().contains("clear")) {
            mood = currentLanguage.equals("English") ? "Sunny: Having Main Character Energy" : "Sonnig: Main-Character-Energy Vibes";
            suggestion = currentLanguage.equals("English") ? "Listen to 'Walking on Sunshine'!" : "Hör dir 'Walking on Sunshine' an!";
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            mood = currentLanguage.equals("English") ? "Cloudy: Feeling Pensive" : "Wolkig: Nachdenklich";
            suggestion = currentLanguage.equals("English") ? "Watch 'The Cloud Atlas'." : "Schau dir 'The Cloud Atlas' an.";
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            mood = currentLanguage.equals("English") ? "Rainy: Feeling Emo" : "Regnerisch: Emo Vibes";
            suggestion = currentLanguage.equals("English") ? "Listen to 'Raindrops Keep Fallin' on My Head'." : "Hören dir 'Raindrops Keep Fallin' on My Head' an.";
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            mood = currentLanguage.equals("English") ? "Snowy: Feeling Cozy" : "Schneebedeckt: Gemütliche Vibes";
            suggestion = currentLanguage.equals("English") ? "Watch 'Frozen'." : "Schau dir 'Frozen' an.";
        } else {
            mood = currentLanguage.equals("English") ? "Unknown: Feeling Curious" : "Unbekannt: Neugierige Vibes";
            suggestion = currentLanguage.equals("English") ? "Explore new music or movies!" : "Entdecke neue Musik oder Filme!";
        }

        moodLabel.setText(mood);
        suggestionLabel.setText(suggestion);
    }

    private void updateBackgroundColor(String weatherDescription) {
        String backgroundColor;

        if (weatherDescription.toLowerCase().contains("clear sky")) {
            backgroundColor = "#B0C4DE";
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            backgroundColor = "#B0C4DE";
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            backgroundColor = "#4682B4";
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            backgroundColor = "#ADD8E6";
        } else {
            backgroundColor = "#2c3e50";
        }

        rootVBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 20; -fx-font-family: 'Arial';");
    }

    private void updateTemperatureUnit(boolean isCelsius) {
        this.isCelsius = isCelsius;
        if (currentTemperatureInCelsius != 0) {
            updateTemperatureLabel(currentTemperatureInCelsius);
        }
    }

    private void updateTemperatureLabel(double temperatureInCelsius) {
        double temperature = isCelsius ? temperatureInCelsius : (temperatureInCelsius * 9 / 5) + 32;
        String unit = isCelsius ? "°C" : "°F";
        temperatureLabel.setText(String.format("%.1f%s", temperature, unit));
    }

    private void updateLanguage(String language) {
        this.currentLanguage = language;
        updateUILabels();

        if (goBackButton.isVisible()) {
            onGetWeatherButtonClick();
        } else if (getForecastButton.isVisible()) {
            onGetForecastButtonClick();
        }
    }

    private void updateUILabels() {
        if (currentLanguage.equals("English")) {
            cityTextField.setPromptText("Enter City Name");
            getWeatherButton.setText("Get Weather");
            goBackButton.setText("Go Back");
            saveSettingsButton.setText("Save Settings");
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");
            englishRadioButton.setText("English");
            germanRadioButton.setText("German");
            settingsTab.setText("Settings");
            settingsTitleLabel.setText("Settings");
            temperatureUnitLabel.setText("Temperature Unit");
            languageLabel.setText("Language");
        } else {
            cityTextField.setPromptText("Stadtname eingeben");
            getWeatherButton.setText("Wetter abrufen");
            goBackButton.setText("Zurück");
            saveSettingsButton.setText("Einstellungen speichern");
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");
            englishRadioButton.setText("Englisch");
            germanRadioButton.setText("Deutsch");
            settingsTab.setText("Einstellungen");
            settingsTitleLabel.setText("Einstellungen");
            temperatureUnitLabel.setText("Temperatureinheit");
            languageLabel.setText("Sprache");
        }
    }
    private String translateWeatherDescription(String description) {
        if (currentLanguage.equals("German")) {
            switch (description.toLowerCase()) {
                case "clear sky":
                    return "Klarer Himmel";
                case "few clouds":
                    return "Wenige Wolken";
                case "scattered clouds":
                    return "Aufgelockerte Bewölkung";
                case "broken clouds":
                    return "Stark bewölkt";
                case "shower rain":
                    return "Regenschauer";
                case "rain":
                    return "Regen";
                case "light rain":
                    return "Leichter Regen";
                case "moderate rain":
                    return "Mäßiger Regen";
                case "heavy intensity rain":
                    return "Starker Regen";
                case "thunderstorm":
                    return "Gewitter";
                case "snow":
                    return "Schnee";
                case "mist":
                    return "Nebel";
                case "haze":
                    return "Dunst";
                case "fog":
                    return "Nebel";
                case "light intensity drizzle":
                    return "Leichter Nieselregen";
                case "overcast clouds":
                    return "Bewölkt";
                default:
                    return description; // Fallback to the original if not matched
            }
        }
        return description; // Return the description as is if language is English
    }
    private String translateHumidity(int humidity) {
        if (currentLanguage.equals("German")) {
            return "Luftfeuchtigkeit: " + humidity + "%";
        }
        return "Humidity: " + humidity + "%";
    }
    private String translateDay(int index) {
        if (currentLanguage.equals("German")) {
            switch (index) {
                case 0: return "Tag 1";
                case 1: return "Tag 2";
                case 2: return "Tag 3";
                case 3: return "Tag 4";
                case 4: return "Tag 5";
                default: return "Tag " + (index + 1); // Fallback
            }
        }
        return "Day " + (index + 1); // Return in English by default
    }

}
