package io.karlis.weatherapp.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.karlis.weatherapp.entities.IpLog;
import io.karlis.weatherapp.entities.WeatherData;
import io.karlis.weatherapp.repositories.IpLogRepository;
import io.karlis.weatherapp.repositories.WeatherRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class WeatherService {
    private final static int CACHE_DURATION = 15;
    private final String IP_API = "http://ip-api.com/json/";
    private final String WEATHER_LINK = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={API_KEY}";
    private final WeatherRepository weatherRepository;
    private final IpLogRepository ipLogRepository;
    private final Cache<String, JSONObject> weatherCache;
    @Value("${API_KEY}")
    private String API_KEY;

    public WeatherService(WeatherRepository weatherRepository, IpLogRepository ipLogRepository) {
        this.ipLogRepository = ipLogRepository;
        this.weatherRepository = weatherRepository;
        this.weatherCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(CACHE_DURATION)).initialCapacity(50).build();

    }

    @SneakyThrows
    public JSONObject getWeather(String ip) {
        if (ip == null) {
            return new JSONObject().put("error", "IP is null").put("status", HttpStatus.BAD_REQUEST);
        }
        if (this.weatherCache.getIfPresent(ip) != null) {
            return this.weatherCache.getIfPresent(ip);
        }
        log.info("Getting weather data for ip");
        RestTemplate restTemplate = new RestTemplate();
        String url = Objects.equals(ip, "0:0:0:0:0:0:0:1") ? IP_API : IP_API + ip;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (!responseEntity.hasBody()) {
            return new JSONObject().put("error", "Response is null from ip-api.com API").put("status", responseEntity.getStatusCode());
        }
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        if (!jsonObject.toMap().containsKey("lat")) {
            return new JSONObject().put("error", "Response returned from ip-api.com API does not contain lat and lon fields").put("status", responseEntity.getStatusCode());
        }
        Double lat = jsonObject.getDouble("lat");
        Double lon = jsonObject.getDouble("lon");

        IpLog ipLog = new IpLog();
        ipLog.setIp(ip);
        ipLog.setQueryTime(LocalDateTime.now());
        ipLog.setCity(jsonObject.getString("city"));
        ipLog.setLatitude(lat);
        ipLog.setLongitude(lon);

        ipLogRepository.save(ipLog);
        JSONObject weatherData = getWeatherByCoordinates(lat, lon);
        weatherCache.put(ip, weatherData);
        return weatherData;
    }

    @SneakyThrows
    private JSONObject getWeatherByCoordinates(Double lat, Double lon) {
        log.info("Getting weather data for city");
        if (weatherRepository.existsByLatitudeAndLongitude(lat, lon)) {
            WeatherData weatherData = weatherRepository.findByLatitudeAndLongitude(lat, lon);
            if (weatherData.getQueryTime().isAfter(LocalDateTime.now().minusMinutes(CACHE_DURATION))) {
                return new JSONObject(weatherData);
            }
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = WEATHER_LINK.replace("{lat}", lat.toString()).replace("{lon}", lon.toString()).replace("{API_KEY}", API_KEY);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getBody() == null) {
            JSONObject errorObject = new JSONObject();
            errorObject.put("message", "Response is null from openweathermap.org API");
            return errorObject;
        }
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode.is2xxSuccessful()) {
            WeatherData weatherData = saveWeatherDataFromResponse(responseEntity.getBody());
            return new JSONObject(weatherData);
        } else {
            JSONObject errorObject = new JSONObject();
            errorObject.put("message", "Error response from openweathermap.org API");
            errorObject.put("status", statusCode.value());
            return errorObject;
        }
    }

    private WeatherData saveWeatherDataFromResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String city = jsonObject.getString("name");
        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        int humidity = jsonObject.getJSONObject("main").getInt("humidity");
        int pressure = jsonObject.getJSONObject("main").getInt("pressure");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        double lat = jsonObject.getJSONObject("coord").getDouble("lat");
        double lon = jsonObject.getJSONObject("coord").getDouble("lon");
        LocalDateTime queryTime = LocalDateTime.now();

        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setLatitude(lat);
        weatherData.setLongitude(lon);
        weatherData.setTemperature(temperature);
        weatherData.setHumidity(humidity);
        weatherData.setPressure(pressure);
        weatherData.setWindSpeed(windSpeed);
        weatherData.setWeatherCondition(weatherDescription);
        weatherData.setQueryTime(queryTime);
        weatherRepository.save(weatherData);
        return weatherData;
    }

    public long getCacheSize() {
        return weatherCache.estimatedSize();
    }

    public void clearCache() {
        weatherCache.invalidateAll();
    }
}
