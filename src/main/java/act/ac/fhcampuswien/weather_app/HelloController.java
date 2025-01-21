package act.ac.fhcampuswien.weather_app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.*;

/**
 * Controller class for managing the Weather App's UI and logic.
 */
public class HelloController {

    // FXML-bound UI components
    @FXML private TextField cityTextField;
    @FXML private ListView<String> listView;
    @FXML private Label cityLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label humidityLabel;
    @FXML private Label moodLabel;
    @FXML private Label suggestionLabel;
    @FXML private ImageView weatherIcon;
    @FXML private VBox rootVBox;
    @FXML private Button goBackButton;
    @FXML private Button getWeatherButton;
    @FXML private Button saveSettingsButton;
    @FXML private RadioButton celsiusRadioButton;
    @FXML private RadioButton fahrenheitRadioButton;
    @FXML private RadioButton englishRadioButton;
    @FXML private RadioButton germanRadioButton;
    @FXML private Tab settingsTab;
    @FXML private Label settingsTitleLabel;
    @FXML private Label temperatureUnitLabel;
    @FXML private Label languageLabel;
    @FXML private GridPane forecastGrid;
    @FXML private Button getForecastButton;
    @FXML private TextField cityInputField;
    @FXML private TextField countryCodeInputField;

    // Internal state variables
    private boolean isCelsius = true;
    private double currentTemperatureInCelsius;
    private String currentLanguage = "English";
    private ObservableList<String> cityList = FXCollections.observableArrayList();
    private String selectedCity = "";

    /**
     * Handles the forecast button click event.
     * Fetches and displays forecast data for the selected city.
     */
    @FXML
    public void onGetForecastButtonClick() {
        if (selectedCity == null || selectedCity.isEmpty()) return;

        String[] cityAndCountryCode = selectedCity.split(";");
        if (cityAndCountryCode.length < 2) return;

        String cityName = cityAndCountryCode[0];
        String countryCode = cityAndCountryCode[1];

        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchForecast(cityName, countryCode);
            WeatherData[] forecastData = api.parseForecastData(jsonResponse);

            clearForecastGrid();

            for (int i = 0; i < forecastData.length; i++) {
                WeatherData data = forecastData[i];
                double temperature = isCelsius ? data.getTemperature() : (data.getTemperature() * 9.0 / 5.0) + 32.0;

                forecastGrid.add(new Label(translateDay(i)), 0, i);
                forecastGrid.add(new Label(String.format("%.1f%s", temperature, isCelsius ? "°C" : "°F")), 1, i);
                forecastGrid.add(new Label(translateWeatherDescription(data.getDescription())), 2, i);
                forecastGrid.add(new Label(translateHumidity(data.getHumidity())), 3, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the forecast grid UI component.
     */
    private void clearForecastGrid() {
        forecastGrid.getChildren().clear();
    }

    /**
     * Initializes the UI components and application state.
     */
    @FXML
    public void initialize() {
        clearWeatherData();
        forecastGrid.getChildren().clear();
        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);
        getForecastButton.setVisible(true);

        applyButtonStyle(getWeatherButton);
        applyButtonStyle(goBackButton);

        initializeToggleGroups();
        initializeListView();

        loadCSVFiles();
    }

    /**
     * Sets up toggle groups for radio buttons and their event handlers.
     */
    private void initializeToggleGroups() {
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
    }

    /**
     * Initializes the ListView and sets up filtering logic.
     */
    private void initializeListView() {
        FilteredList<String> filteredList = new FilteredList<>(cityList, s -> true);
        listView.setItems(filteredList);

        cityTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(city -> newValue == null || newValue.isEmpty() || city.toLowerCase().contains(newValue.toLowerCase())));

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, userSelection) -> {
            if (userSelection != null) {
                selectedCity = userSelection;
            }
        });
    }

    /**
     * Loads city data from a CSV file into the city list.
     */
    private void loadCSVFiles() {
        try (InputStream input = getClass().getResourceAsStream("/data/country_codes.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cityList.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies a consistent style to buttons.
     *
     * @param button the button to style
     */
    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");

        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #5A9BD4; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"));
    }

    /**
     * Handles the weather button click event.
     * Fetches and displays weather data for the selected city.
     */
    @FXML
    public void onGetWeatherButtonClick() {
        if (selectedCity.isEmpty()) return;

        String[] cityAndCountryCode = selectedCity.split(";");
        String cityName = cityAndCountryCode[0];
        String countryCode = cityAndCountryCode[1];

        clearWeatherData();

        if (cityName.isEmpty() || countryCode.isEmpty()) {
            cityLabel.setText(getLocalizedText("Please enter a city and country code!", "Bitte gib eine Stadt und einen Ländercode ein!"));
            return;
        }

        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchWeather(cityName, countryCode);
            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            updateWeatherUI(weatherData);
        } catch (Exception e) {
            cityLabel.setText(getLocalizedText("Error: Could not fetch data for " + cityName, "Fehler: Daten für " + cityName + " konnten nicht abgerufen werden"));
            e.printStackTrace();
        }
    }

    /**
     * Handles the go-back button click event.
     * Resets the UI to the initial state.
     */
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

    /**
     * Clears weather data from the UI components.
     */
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

    /**
     * Updates the UI with fetched weather data.
     *
     * @param weatherData the weather data to display
     */
    private void updateWeatherUI(WeatherData weatherData) {
        cityLabel.setText(weatherData.getCityName());
        currentTemperatureInCelsius = weatherData.getTemperature();
        updateTemperatureLabel(currentTemperatureInCelsius);
        descriptionLabel.setText(translateWeatherDescription(weatherData.getDescription()));
        humidityLabel.setText(translateHumidity(weatherData.getHumidity()));
        updateWeatherIcon(weatherData.getDescription());
        assignMoodAndSuggestion(weatherData.getDescription());
        updateBackgroundColor(weatherData.getDescription());

        toggleUIVisibilityForWeatherDetails();
    }

    /**
     * Toggles UI visibility when displaying weather details.
     */
    private void toggleUIVisibilityForWeatherDetails() {
        getForecastButton.setVisible(false);
        goBackButton.setVisible(true);
        getWeatherButton.setVisible(false);
        cityTextField.setVisible(false);
        listView.setVisible(false);
    }

    /**
     * Returns localized text based on the current language.
     *
     * @param englishText the English version of the text
     * @param germanText the German version of the text
     * @return the text in the current language
     */
    private String getLocalizedText(String englishText, String germanText) {
        return currentLanguage.equals("English") ? englishText : germanText;
    }

    /**
     * Updates the weather icon based on the description.
     *
     * @param weatherDescription the description of the weather
     */
    private void updateWeatherIcon(String weatherDescription) {
        String iconFileName;

        switch (weatherDescription.toLowerCase()) {
            case "clear sky": iconFileName = "clear.png"; break;
            case "few clouds":
            case "scattered clouds":
            case "broken clouds":
            case "overcast clouds": iconFileName = "cloudy.png"; break;
            case "shower rain":
            case "rain":
            case "light intensity drizzle": iconFileName = "rain.png"; break;
            case "thunderstorm": iconFileName = "cloudy.png"; break;
            case "snow": iconFileName = "snow.png"; break;
            case "mist": iconFileName = "humidity.png"; break;
            default: iconFileName = "default.png";
        }

        try {
            Image icon = new Image(getClass().getResource("/icons/" + iconFileName).toExternalForm());
            weatherIcon.setImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon for description: " + weatherDescription + ". File not found: " + iconFileName);
        }
    }

    /**
     * Assigns mood and suggestion text based on the weather description.
     *
     * @param weatherDescription the description of the weather
     */
    private void assignMoodAndSuggestion(String weatherDescription) {
        String mood;
        String suggestion;

        if (weatherDescription.toLowerCase().contains("clear")) {
            mood = getLocalizedText("Sunny: Having Main Character Energy", "Sonnig: Main-Character-Energy Vibes");
            suggestion = getLocalizedText("Listen to 'Walking on Sunshine'!", "Hör dir 'Walking on Sunshine' an!");
        } else if (weatherDescription.toLowerCase().contains("cloud")) {
            mood = getLocalizedText("Cloudy: Feeling Pensive", "Wolkig: Nachdenklich");
            suggestion = getLocalizedText("Watch 'The Cloud Atlas'.", "Schau dir 'The Cloud Atlas' an.");
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            mood = getLocalizedText("Rainy: Feeling Emo", "Regnerisch: Emo Vibes");
            suggestion = getLocalizedText("Listen to 'Raindrops Keep Fallin' on My Head'.", "Hören dir 'Raindrops Keep Fallin' on My Head' an.");
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            mood = getLocalizedText("Snowy: Feeling Cozy", "Schneebedeckt: Gemütliche Vibes");
            suggestion = getLocalizedText("Watch 'Frozen'.", "Schau dir 'Frozen' an.");
        } else {
            mood = getLocalizedText("Unknown: Feeling Curious", "Unbekannt: Neugierige Vibes");
            suggestion = getLocalizedText("Explore new music or movies!", "Entdecke neue Musik oder Filme!");
        }

        moodLabel.setText(mood);
        suggestionLabel.setText(suggestion);
    }

    /**
     * Updates the background color based on the weather description.
     *
     * @param weatherDescription the description of the weather
     */
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

    /**
     * Updates the temperature unit based on user selection.
     *
     * @param isCelsius true if the unit is Celsius, false for Fahrenheit
     */
    private void updateTemperatureUnit(boolean isCelsius) {
        this.isCelsius = isCelsius;
        if (currentTemperatureInCelsius != 0) {
            updateTemperatureLabel(currentTemperatureInCelsius);
        }
    }

    /**
     * Updates the temperature label in the UI.
     *
     * @param temperatureInCelsius the temperature in Celsius
     */
    private void updateTemperatureLabel(double temperatureInCelsius) {
        double temperature = isCelsius ? temperatureInCelsius : (temperatureInCelsius * 9 / 5) + 32;
        temperatureLabel.setText(String.format("%.1f%s", temperature, isCelsius ? "°C" : "°F"));
    }

    /**
     * Updates the language for the UI and reloads labels accordingly.
     *
     * @param language the selected language
     */
    private void updateLanguage(String language) {
        this.currentLanguage = language;
        updateUILabels();

        if (goBackButton.isVisible()) {
            onGetWeatherButtonClick();
        } else if (getForecastButton.isVisible()) {
            onGetForecastButtonClick();
        }
    }

    /**
     * Updates UI labels to match the current language.
     */
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

    /**
     * Translates the given weather description to the current language.
     *
     * @param description the weather description in English
     * @return the translated description if the current language is German,
     *         otherwise returns the original description
     */
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
                    return description;
            }
        }
        return description;
    }

    /**
     * Translates the humidity value to the current language.
     *
     * @param humidity the humidity percentage
     * @return a formatted string for humidity in the current language
     */
    private String translateHumidity(int humidity) {
        return currentLanguage.equals("German")
                ? "Luftfeuchtigkeit: " + humidity + "%"
                : "Humidity: " + humidity + "%";
    }

    /**
     * Translates the day index to the current language.
     *
     * @param index the index of the day (starting from 0)
     * @return a formatted string for the day in the current language
     */
    private String translateDay(int index) {
        return currentLanguage.equals("German")
                ? "Tag " + (index + 1)
                : "Day " + (index + 1);
    }
}
