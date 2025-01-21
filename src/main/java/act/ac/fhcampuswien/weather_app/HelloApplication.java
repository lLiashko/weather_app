package act.ac.fhcampuswien.weather_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main application class for the Weather App.
 * This class initializes and launches the JavaFX application.
 */
public class HelloApplication extends Application {

    /**
     * Entry point for the JavaFX application.
     * Loads the FXML layout, applies the stylesheet, and sets up the primary stage.
     *
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file or stylesheet cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file for the main UI layout
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Create the scene with specified dimensions and load the FXML content
        Scene scene = new Scene(fxmlLoader.load(), 900, 900);

        // Apply the dark theme stylesheet to the scene
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/dark-theme.css")).toExternalForm());

        // Set the title of the application window
        stage.setTitle("Weather App");

        // Attach the scene to the stage and display the stage
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main entry point for the application.
     * Initializes the WeatherAPI, fetches weather data for Vienna, and launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Instantiate the WeatherAPI to fetch weather data
        WeatherAPI api = new WeatherAPI();

        try {
            // Fetch weather data for Vienna, Austria
            String weatherData = api.fetchWeather("Vienna", "at");

            // Print the fetched weather data to the console (for debugging purposes)
            System.out.println(weatherData);
        } catch (Exception e) {
            // Print the stack trace if an error occurs while fetching weather data
            e.printStackTrace();
        }

        // Launch the JavaFX application
        launch();
    }
}
