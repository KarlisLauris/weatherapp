package io.karlis.weatherapp.repositories;

import io.karlis.weatherapp.entities.IpLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpLogRepository extends JpaRepository<IpLog,String> {
}
