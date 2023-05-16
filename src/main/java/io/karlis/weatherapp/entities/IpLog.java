package io.karlis.weatherapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ip_log")
@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        IpLog ipLog = (IpLog) o;
        return getId() != null && Objects.equals(getId(), ipLog.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
