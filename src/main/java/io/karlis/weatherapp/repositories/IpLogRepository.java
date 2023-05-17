package io.karlis.weatherapp.repositories;

import io.karlis.weatherapp.entities.IpLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface IpLogRepository extends JpaRepository<IpLog, String> {
    @Query("select (count(i) > 0) from IpLog i where i.ip = ?1")
    boolean existsByIp(@NonNull String ip);
}
