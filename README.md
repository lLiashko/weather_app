# Weather App 🌤️
This is a **JavaFX-based weather application** that fetches weather data for a specified city and displays it in a user-friendly interface. The app uses the [OpenWeatherMap API](https://openweathermap.org/api) to retrieve weather information such as temperature, humidity, and a general weather description.

## Features ✨
- Fetches current weather data for any city in the world.
- Displays:
    - **City Name**
    - **Temperature (in Celsius)**
    - **Humidity**
    - **Weather Description**

- User-friendly interface built with JavaFX.
- Handles errors gracefully, such as invalid city names or API issues.

## 📱 App Screenshots
### 🔍 Main Screen – City Search
On the main screen, users can search for a city to view current weather conditions. The search bar allows real-time suggestions as the user types.
![Screenshot 2025-05-06 at 11 49 23](https://github.com/user-attachments/assets/27f0059f-8b20-4167-9d82-31211781b844)

### 🌤️ Weather Screen
After selecting a city, the weather screen displays the current weather, temperature, and additional data like humidity and some funny suggestions what to do during this weather.
![Screenshot 2025-05-06 at 11 49 53](https://github.com/user-attachments/assets/05a98a0d-37fc-456f-95a2-833ee65b843e)

### ⚙️ Settings Screen
The settings screen lets users customize units (Celsius/Fahrenheit) and language.
![Screenshot 2025-05-06 at 11 49 33](https://github.com/user-attachments/assets/46c4056d-8c92-480c-afc7-ea6e51d8a80f)

## Prerequisites ⚙️
Before running the project, make sure you have the following installed:
1. **Java Development Kit (JDK)**: Requires **Java 21** (or newer).
2. **Maven or Gradle**: For dependency management (if applicable).
3. **Internet Connection**: The app fetches real-time weather data from the OpenWeatherMap API.
4. **API Key**: You need an API key from OpenWeatherMap.

## Setup 🛠️
### 1. Clone the Repository
``` bash
git clone https://github.com/your-username/weather-app.git
cd weather-app
```
### 2. Add Dependencies (Optional)
Ensure that any required libraries (like Gson) are available in your project. If you're using **Gradle** as your build tool, add the following dependency for **Gson** in your `build.gradle` file:
``` gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.8.9'
}
```
If you're using a dependency manager like Maven, add this dependency for **Gson** in your `pom.xml`:
``` xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10</version>
</dependency>
```
### 3. Set the API Key
a. **Create an OpenWeatherMap Account**:
- Go to [OpenWeatherMap](https://openweathermap.org/api) and register.
- After registration, generate an API key in your account dashboard.

b. **Set the API Key in Your Environment Variables**:
1. Add an environment variable named `OPEN_WEATHER_API_KEY` and set its value to your API key.
    - On **Windows**:
``` cmd
     setx OPEN_WEATHER_API_KEY your_api_key_here
```
- On **macOS/Linux** (add this to your `~/.bashrc` or `~/.zshrc`):
``` bash
     export OPEN_WEATHER_API_KEY=your_api_key_here
```
1. Restart your terminal or IDE to make sure the changes take effect.

### 4. Build and Run the Project
Compile and run the app using your IDE (e.g., IntelliJ IDEA) or Java CLI.
To run the app from the terminal:
``` bash
cd src
javac act/ac/fhcampuswien/weather_app/*.java
java act.ac.fhcampuswien.weather_app.HelloApplication
```
## How to Use 🚀
1. Launch the application.
2. Enter the name of a city in the input text field.
3. Provide the country code in a separate field.
4. Click the **Get Weather** button.
5. View the weather data, including:
    - City name
    - Temperature (in Celsius)
    - Humidity
    - Description of the current weather (e.g., clear sky, rain, etc.)

6. If there's an error (e.g., invalid city name), an error message will display instead.

## Code Overview 🧑‍💻
### Project Structure
``` plaintext
src/
├── act.ac.fhcampuswien.weather_app
│   ├── WeatherAPI.java         // Handles API requests and JSON parsing
│   ├── WeatherData.java        // Represents weather data as an object
│   ├── HelloController.java    // Manages JavaFX UI interactions
│   ├── HelloApplication.java   // Launches the JavaFX application
├── resources/
│   ├── hello-view.fxml         // Defines the JavaFX UI layout
│   ├── application.css         // (Optional) Styles for the JavaFX UI
```
### Key Files
1. **WeatherAPI.java**:
    - Connects to the OpenWeatherMap API and retrieves weather data.
    - Parses JSON responses into structured `WeatherData` objects.

2. **WeatherData.java**:
    - A plain Java object (POJO) holding weather-related data: temperature, humidity, description, city name, and country code.

3. **HelloController.java**:
    - Interacts with the UI elements (labels, text fields, and buttons).
    - Updates the UI with weather data or error messages.

4. **HelloApplication.java**:
    - JavaFX application entry point that initializes the scene and application window.

## Known Issues 🐞
1. Limited Error Handling:
    - If a city is invalid or there are API errors, the feedback is basic.

2. Blocking API Calls:
    - The API request is made on the main thread, causing the UI to freeze for a few seconds during slow network responses.

**Future Enhancements:**
- Implement asynchronous API calls using `Task` or `CompletableFuture` to improve UI responsiveness.
- Add detailed error feedback for users.
- Support additional weather parameters such as wind speed, sunset/sunrise times, etc.

## Dependencies 📦
- **JavaFX**: For UI rendering.
- **Gson**: For parsing JSON responses from OpenWeatherMap API.

## Acknowledgements 🙏
- [OpenWeatherMap](https://openweathermap.org/api) for their powerful weather API.
- [JavaFX](https://openjfx.io/) for the excellent UI framework.
