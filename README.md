
# WeatherApp

The WeatherApp is a RESTful API that provides current weather data for a given IP address. This application allows users to retrieve weather information based on their IP location. It utilizes the OpenWeatherMap API to fetch weather data for a specific latitude and longitude. The WeatherApp is built using Spring Boot and implements caching to improve performance and minimize API calls, and uses in-memory H2 database.


## Installation

1. Clone the repository:```git clone https://github.com/KarlisLauris/weatherapp.git```
2. Set your OpenWeatherMap API key and database properties in the ```application.properties```file
3. Install all of the maven dependencies
## API Reference

#### Get all items

```http
  GET /weather
```






## Testing
All of the tests are located in ```src/test/java/io/karlis/weatherapp/WeatherApplicationTests.java``` and can be ran from there
## Author

- [@KarlisLauris](https://www.github.com/KarlisLauris)

