package io.karlis.weatherapp.controllers;

import io.karlis.weatherapp.services.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {

    final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    private static String getIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeather(HttpServletRequest request) {
        String ip = getIp(request);
        JSONObject weather = service.getWeather(ip);
        return ResponseEntity.ok(weather.toString(1));
    }

}
