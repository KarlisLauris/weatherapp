package io.karlis.weatherapp.entities;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ip_log")
@Setter
@RequiredArgsConstructor
public class IpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "city")
    private String city;

    @Column(name = "query_time")
    private LocalDateTime queryTime;

}
