package act.ac.fhcampuswien.weather_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 920, 640);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/dark-theme.css")).toExternalForm());
        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        WeatherAPI api = new WeatherAPI();
        try {
            String weatherData = api.fetchWeather("Vienna", "at");
            System.out.println(weatherData); // For debugging, later pass it to the UI
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch();
    }
}