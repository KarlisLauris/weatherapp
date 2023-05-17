package io.karlis.weatherapp;

import io.karlis.weatherapp.controllers.WeatherController;
import io.karlis.weatherapp.entities.IpLog;
import io.karlis.weatherapp.entities.WeatherData;
import io.karlis.weatherapp.repositories.IpLogRepository;
import io.karlis.weatherapp.repositories.WeatherRepository;
import io.karlis.weatherapp.services.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class WeatherApplicationTests {
    @Autowired
    private WeatherController controller;
    @Autowired
    private WeatherService service;
    @Mock
    private WeatherRepository weatherRepository;
    @Mock
    private IpLogRepository ipLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
		this.service.clearCache();
    }

    @Test
    void returnsWeatherData() {
        ResponseEntity<String> response = controller.getWeather(null);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("weather"));
    }

    @Test
    void cachesWeatherData() {
        String ip = "0:0:0:0:0:0:0:1";
        this.service.getWeather(ip);
        this.service.getWeather(ip);
        Assertions.assertEquals(1, service.getCacheSize());
    }

    @Test
    void savesIpAndWeatherLog() {
        IpLog ipLog = new IpLog();
        ipLog.setLatitude(0.0);
        ipLog.setLongitude(0.0);
        ipLog.setCity("Riga");
        ipLog.setQueryTime(LocalDateTime.now());

        WeatherData weatherData = new WeatherData();
        weatherData.setCity("Riga");
        weatherData.setTemperature(0.0);
        weatherData.setHumidity(1);
        weatherData.setWindSpeed(2.0);
        weatherData.setLatitude(0.0);
        weatherData.setLongitude(0.0);
        weatherData.setWeatherCondition("Clear");
        weatherData.setQueryTime(LocalDateTime.now());

		when(weatherRepository.save(Mockito.any(WeatherData.class))).thenReturn(weatherData);
        when(ipLogRepository.save(Mockito.any(IpLog.class))).thenReturn(ipLog);

        String ip = "0:0:0:0:0:0:0:1";

        when(ipLogRepository.existsByIp(Mockito.anyString())).thenReturn(false);
		when(weatherRepository.existsByLatitudeAndLongitude(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(false);
        when(ipLogRepository.findAll()).thenReturn(Collections.singletonList(ipLog));
		when(weatherRepository.findAll()).thenReturn(Collections.singletonList(weatherData));

        this.service.getWeather(ip);
        Assertions.assertTrue(ipLogRepository.findAll().size() > 0);
		Assertions.assertTrue(weatherRepository.findAll().size() > 0);
    }
}
