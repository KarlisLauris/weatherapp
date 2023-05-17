package io.karlis.weatherapp.controllers;

import io.karlis.weatherapp.services.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.karlis.weatherapp.utils.GetIP.getIP;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeather(HttpServletRequest request) {
        String ip = getIP(request);
        JSONObject weather = service.getWeather(ip);
        return ResponseEntity.ok(weather.toString(1));
    }

}
