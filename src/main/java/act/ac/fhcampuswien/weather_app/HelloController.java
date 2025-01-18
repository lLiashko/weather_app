package act.ac.fhcampuswien.weather_app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.*;

public class HelloController {
//    @FXML
//    private TextField countryTextField;

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
    private Button saveSettingsButton; // Added missing declaration

    @FXML
    private RadioButton celsiusRadioButton;

    @FXML
    private RadioButton fahrenheitRadioButton;

    @FXML
    private RadioButton englishRadioButton;

    @FXML
    private RadioButton germanRadioButton;

    @FXML
    private Tab settingsTab; // Added missing declaration

    @FXML
    private Label settingsTitleLabel; // Added missing declaration

    @FXML
    private Label temperatureUnitLabel; // Added missing declaration

    @FXML
    private Label languageLabel; // Added missing declaration

    private boolean isCelsius = true; // Default to Celsius
    private double currentTemperatureInCelsius; // Store the current temperature in Celsius
    private String currentLanguage = "English"; // Default to English
    private ObservableList<String> cityList = FXCollections.observableArrayList();
    private String selectedCity = "";

    @FXML
    public void initialize() {
        clearWeatherData();
        goBackButton.setVisible(false);
        getWeatherButton.setVisible(true);

        // Apply styling to match the main theme
        applyButtonStyle(getWeatherButton);
        applyButtonStyle(goBackButton);

        // Group the temperature radio buttons
        ToggleGroup temperatureToggleGroup = new ToggleGroup();
        celsiusRadioButton.setToggleGroup(temperatureToggleGroup);
        fahrenheitRadioButton.setToggleGroup(temperatureToggleGroup);
        celsiusRadioButton.setSelected(true);

        // Group the language radio buttons
        ToggleGroup languageToggleGroup = new ToggleGroup();
        englishRadioButton.setToggleGroup(languageToggleGroup);
        germanRadioButton.setToggleGroup(languageToggleGroup);
        englishRadioButton.setSelected(true);

        // Add action listeners
        celsiusRadioButton.setOnAction(event -> updateTemperatureUnit(true));
        fahrenheitRadioButton.setOnAction(event -> updateTemperatureUnit(false));
        englishRadioButton.setOnAction(event -> updateLanguage("English"));
        germanRadioButton.setOnAction(event -> updateLanguage("German"));
        loadCSVFiles();

        // Create a FilteredList that wraps the cityList
        FilteredList<String> filteredList = new FilteredList<>(cityList, s -> true);

        // Bind the filtered list to the ListView
        listView.setItems(filteredList);

        /*cityTextField.textProperty().addListener((observable, oldValue, newValue) ->{
            listView.setItems(cityList.filtered(item -> item.toLowerCase().contains(newValue.toLowerCase())));
        } );*/

        // Add a listener to the search field to filter the list dynamically
        cityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(city -> {
                // If the search field is empty, display all cities
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare the city names with the search text (case-insensitive)
                return city.toLowerCase().contains(newValue.toLowerCase());
            });
        });

        // Handle selection of items in the ListView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, userSelection) -> {
            if (userSelection != null) {
                this.selectedCity=userSelection;
         }});
    }

    private void loadCSVFiles(){
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

    // Apply consistent button styling
    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #4682B4; " +  // Same as 'Get Weather' button
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
        if(this.selectedCity.isEmpty()){
            return;
        }
        String[] cityAndCountryCode = this.selectedCity.split(";");
        String cityName = cityAndCountryCode[0];
        // TODO Do it
        String countryCode = cityAndCountryCode[1];

        System.out.println(cityAndCountryCode[0] + " " + cityAndCountryCode[1]);

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
            descriptionLabel.setText(translateWeatherDescription(weatherData.getDescription())); // Set translated description
            humidityLabel.setText(currentLanguage.equals("English") ? "Humidity: " + weatherData.getHumidity() + "%" : "Luftfeuchtigkeit: " + weatherData.getHumidity() + "%");

            updateWeatherIcon(weatherData.getDescription());
            assignMoodAndSuggestion(weatherData.getDescription());
            updateBackgroundColor(weatherData.getDescription());

            // Show the "Go Back" button and hide "Get Weather" button
            goBackButton.setVisible(true);
            getWeatherButton.setVisible(false);
            cityTextField.setVisible(false);
            //countryTextField.setVisible(false);
            listView.setVisible(false);

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
//        countryTextField.setVisible(true);
        listView.setVisible(true);

        // Reset temperature unit to default (Celsius)
        celsiusRadioButton.setSelected(true);
        updateTemperatureUnit(true);
    }

    private void clearWeatherData() {
        // Clear all label texts, reset weather icon, and remove background styling
        cityLabel.setText("");
        temperatureLabel.setText(""); // Clear the temperature label
        descriptionLabel.setText("");
        humidityLabel.setText("");
        moodLabel.setText("");
        suggestionLabel.setText("");

        weatherIcon.setImage(null); // Reset weather icon
        rootVBox.setStyle(""); // Reset background color

        currentTemperatureInCelsius = 0.0; // Reset stored temperature
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
                iconFileName = "humidity.png";  // Use humidity icon for mist
                break;
            default:
                iconFileName = "default.png";  // Fallback icon
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
        } else if (weatherDescription.toLowerCase().contains("light intensity drizzle")
                || weatherDescription.toLowerCase().contains("drizzle")) {
            mood = currentLanguage.equals("English") ? "Light Drizzle: Feeling Calm" : "Leichter Nieselregen: Ruhige Stimmung";
            suggestion = currentLanguage.equals("English") ? "Enjoy a warm drink and relax." : "Genieß ein warmes Getränk und entspanne dich.";
        } else if (weatherDescription.toLowerCase().contains("rain")) {
            mood = currentLanguage.equals("English") ? "Rainy: Feeling Emo" : "Regnerisch: Emo Vibes";
            suggestion = currentLanguage.equals("English") ? "Listen to 'Raindrops Keep Fallin' on My Head'." : "Hören dir 'Raindrops Keep Fallin' on My Head' an.";
        } else if (weatherDescription.toLowerCase().contains("snow")) {
            mood = currentLanguage.equals("English") ? "Snowy: Feeling Cozy" : "Schneebedeckt: Gemütliche Vibes";
            suggestion = currentLanguage.equals("English") ? "Watch 'Frozen'." : "Schau dir 'Frozen' an.";
        } else if (weatherDescription.toLowerCase().contains("clear sky")) {
            mood = currentLanguage.equals("English") ? "Perfect day for a walk!" : "Perfekter Tag für einen Spaziergang!";
            suggestion = currentLanguage.equals("English") ? "Enjoy the sunshine!" : "Genieß den Sonnenschein!";
        } else if (weatherDescription.toLowerCase().contains("few clouds")) {
            mood = currentLanguage.equals("English") ? "Great weather for outdoor fun!" : "Tolles Wetter für Aktivitäten im Freien!";
            suggestion = currentLanguage.equals("English") ? "Have fun outside!" : "Viel Spaß draußen!";
        } else if (weatherDescription.toLowerCase().contains("mist")) {
            mood = currentLanguage.equals("English") ? "A mysterious vibe... perfect for reading or cozy drinks." : "Eine geheimnisvolle Stimmung... perfekt zum Lesen oder für gemütliche Getränke.";
            suggestion = currentLanguage.equals("English") ? "Enjoy a cozy time indoors." : "Genieß eine gemütliche Zeit drinnen.";
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
            backgroundColor = "#B0C4DE"; // Same as cloudy (light steel blue)
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
        updateUILabels();  // WICHTIG: Labels aktualisieren
    }

    private void updateUILabels() {
        if (currentLanguage.equals("English")) {
            // Input placeholders
            cityTextField.setPromptText("Enter City Name");
//            countryTextField.setPromptText("Enter Country Code (e.g., AT)");

            // Buttons
            getWeatherButton.setText("Get Weather");
            goBackButton.setText("Go Back");
            saveSettingsButton.setText("Save Settings");

            // Temperature Unit
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");

            // Language Options
            englishRadioButton.setText("English");
            germanRadioButton.setText("German");

            // Settings Tab Labels
            settingsTab.setText("Settings");                // Tab Title
            settingsTitleLabel.setText("Settings");         // Title in Settings
            temperatureUnitLabel.setText("Temperature Unit");  // Temperature Unit Label
            languageLabel.setText("Language");             // Language Label
        } else {
            // Input placeholders
            cityTextField.setPromptText("Stadtname eingeben");
//            countryTextField.setPromptText("Ländercode eingeben (z.B., AT)");

            // Buttons
            getWeatherButton.setText("Wetter abrufen");
            goBackButton.setText("Zurück");
            saveSettingsButton.setText("Einstellungen speichern");

            // Temperature Unit
            celsiusRadioButton.setText("Celsius");
            fahrenheitRadioButton.setText("Fahrenheit");

            // Language Options
            englishRadioButton.setText("Englisch");
            germanRadioButton.setText("Deutsch");

            // Settings Tab Labels
            settingsTab.setText("Einstellungen");               // Tab Title
            settingsTitleLabel.setText("Einstellungen");        // Title in Settings
            temperatureUnitLabel.setText("Temperatureinheit");  // Temperature Unit Label
            languageLabel.setText("Sprache");                  // Language Label
        }
    }

    /**
     * Translates the weather description based on the current language setting.
     *
     * @param description The original weather description from the API.
     * @return The translated weather description.
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
                default:
                    return description; // Fallback to the original if not matched
            }
        }
        return description; // Return original description if language is English
    }
}