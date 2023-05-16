package io.karlis.weatherapp.services;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Slf4j

public class WeatherService {

    @Value("${API_KEY}")
    private String API_KEY;

    @Cacheable(value = "weather", key = "#ip")
    public JSONObject getWeather(String ip) {
        log.info("Getting weather data for ip");
        RestTemplate restTemplate = new RestTemplate();
        String url = Objects.equals(ip, "0:0:0:0:0:0:0:1") ? "http://ip-api.com/json/" : "http://ip-api.com/json/" + ip;
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response is null from ip-api.com API");
        }
        JSONObject jsonObject = new JSONObject(response);
        String lat = jsonObject.getNumber("lat").toString();
        String lon = jsonObject.getNumber("lon").toString();

        return getWeatherByCoordinates(lat, lon);
    }
    private JSONObject getWeatherByCoordinates( String lat, String lon){
        log.info("Getting weather data for city");
        String WEATHER_LINK = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={API_KEY}";

        RestTemplate restTemplate = new RestTemplate();
        String url = WEATHER_LINK.replace("{lat}", lat).replace("{lon}", lon).replace("{API_KEY}", API_KEY);
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response is null from openweathermap.org API");
        }
        return new JSONObject(response);
    }

    @CacheEvict(value = "weather", allEntries = true)
    @Scheduled(fixedRate = 900000)
    public void clearCache() {
        log.info("Clearing cache");
    }


}
