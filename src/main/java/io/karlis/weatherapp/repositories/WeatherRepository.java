package io.karlis.weatherapp.repositories;

import io.karlis.weatherapp.entities.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    @Query("select (count(w) > 0) from WeatherData w where w.latitude = ?1 and w.longitude = ?2")
    boolean existsByLatitudeAndLongitude(@NonNull Double latitude, @NonNull Double longitude);

    WeatherData findByLatitudeAndLongitude(double parseDouble, double parseDouble1);
}
