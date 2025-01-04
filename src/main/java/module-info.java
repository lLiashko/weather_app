module act.ac.fhcampuswien.weather_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens act.ac.fhcampuswien.weather_app to javafx.fxml;
    exports act.ac.fhcampuswien.weather_app;
}