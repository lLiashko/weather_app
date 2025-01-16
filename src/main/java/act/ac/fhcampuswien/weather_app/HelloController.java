package act.ac.fhcampuswien.weather_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

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

    @FXML
    private Button goBackButton;

    @FXML
    private Button getWeatherButton;

    @FXML
    private RadioButton celsiusRadioButton;

    @FXML
    private RadioButton fahrenheitRadioButton;

    @FXML
    private RadioButton englishRadioButton;

    @FXML
    private RadioButton germanRadioButton;

    private boolean isCelsius = true; // Default to Celsius
    private double currentTemperatureInCelsius; // Store the current temperature in Celsius
    private String currentLanguage = "English"; // Default to English

    @FXML
    public void initialize() {
        // Clear all fields and ensure get weather/back button visibility
        clearWeatherData();
        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);

        // Group the temperature radio buttons
        ToggleGroup temperatureToggleGroup = new ToggleGroup();
        celsiusRadioButton.setToggleGroup(temperatureToggleGroup);
        fahrenheitRadioButton.setToggleGroup(temperatureToggleGroup);

        // Set default temperature selection
        celsiusRadioButton.setSelected(true);

        // Add listeners to temperature radio buttons
        celsiusRadioButton.setOnAction(event -> updateTemperatureUnit(true));
        fahrenheitRadioButton.setOnAction(event -> updateTemperatureUnit(false));

        // Group the language radio buttons
        ToggleGroup languageToggleGroup = new ToggleGroup();
        englishRadioButton.setToggleGroup(languageToggleGroup);
        germanRadioButton.setToggleGroup(languageToggleGroup);

        // Set default language selection
        englishRadioButton.setSelected(true);

        // Add listeners to language radio buttons
        englishRadioButton.setOnAction(event -> updateLanguage("English"));
        germanRadioButton.setOnAction(event -> updateLanguage("German"));
    }

    @FXML
    public void onGetWeatherButtonClick() {
        String cityName = cityTextField.getText().trim();
        String countryCode = countryTextField.getText().trim();

        // Clear old data on button click
        clearWeatherData();

        if (cityName.isEmpty() || countryCode.isEmpty()) {
            cityLabel.setText(currentLanguage.equals("English") ? "Please enter a city and country code!" : "Bitte gib eine Stadt und einen Ländercode ein!");
            return;
        }

        try {
            // Fetch weather data
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.fetchWeather(cityName, countryCode);
            WeatherData weatherData = api.parseWeatherData(jsonResponse);

            // Update UI with fetched data
            cityLabel.setText(weatherData.getCityName());
            currentTemperatureInCelsius = weatherData.getTemperature();
            updateTemperatureLabel(currentTemperatureInCelsius);
            descriptionLabel.setText(weatherData.getDescription());
            humidityLabel.setText(currentLanguage.equals("English") ? "Humidity: " + weatherData.getHumidity() + "%" : "Luftfeuchtigkeit: " + weatherData.getHumidity() + "%");

            updateWeatherIcon(weatherData.getDescription());
            assignMoodAndSuggestion(weatherData.getDescription());
            updateBackgroundColor(weatherData.getDescription());

            // Show the "Go Back" button and hide "Get Weather" button
            goBackButton.setVisible(true);
            getWeatherButton.setVisible(false);
            cityTextField.setVisible(false);
            countryTextField.setVisible(false);

        } catch (Exception e) {
            cityLabel.setText(currentLanguage.equals("English") ? "Error: Could not fetch data for " + cityName : "Fehler: Daten für " + cityName + " konnten nicht abgerufen werden");
            e.printStackTrace();
        }
    }

    @FXML
    public void onGoBackButtonClick() {
        // Clear weather data and reset view
        clearWeatherData();

        // Reset visibility
        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);
        cityTextField.setVisible(true);
        countryTextField.setVisible(true);
    }

    private void clearWeatherData() {
        // Clear all label texts, reset weather icon, and remove background styling
        cityLabel.setText("");
        temperatureLabel.setText("");
        descriptionLabel.setText("");
        humidityLabel.setText("");
        moodLabel.setText("");
        suggestionLabel.setText("");

        weatherIcon.setImage(null); // Reset weather icon
        rootVBox.setStyle(""); // Reset background color
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
            mood = currentLanguage.equals("English") ? "Sunny: Having Main Character Energy" : "Sonnig: Main-Character-Energy Vibes :D";
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

        if (weatherDescription.toLowerCase().contains("clear")) {
            backgroundColor = "#fab005"; // Darker but bright yellow (goldenrod)
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

    private void updateTemperatureUnit(boolean isCelsius) {
        this.isCelsius = isCelsius;
        if (currentTemperatureInCelsius != 0) {
            updateTemperatureLabel(currentTemperatureInCelsius);
        }
    }

    private void updateTemperatureLabel(double temperatureInCelsius) {
        double temperature = isCelsius ? temperatureInCelsius : (temperatureInCelsius * 9/5) + 32;
        String unit = isCelsius ? "°C" : "°F";
        temperatureLabel.setText(String.format("%.1f%s", temperature, unit));
    }

    private void updateLanguage(String language) {
        this.currentLanguage = language;
        updateUILabels();
    }

    private void updateUILabels() {
        if (currentLanguage.equals("English")) {
            cityTextField.setPromptText("Enter City Name");
            countryTextField.setPromptText("Enter Country Code (e.g., AT)");
            getWeatherButton.setText("Get Weather");
            goBackButton.setText("Go Back");
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");
            englishRadioButton.setText("English");
            germanRadioButton.setText("German");
        } else {
            cityTextField.setPromptText("Stadtname eingeben");
            countryTextField.setPromptText("Ländercode eingeben (z.B., AT)");
            getWeatherButton.setText("Wetter abrufen");
            goBackButton.setText("Zurück");
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");
            englishRadioButton.setText("Englisch");
            germanRadioButton.setText("Deutsch");
        }
    }
}