package com.academy.airport.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Airplane {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "model", nullable = false, length = 128)
    private String model;
    @Basic
    @Column(name = "aircompany_id", nullable = false)
    private Integer aircompanyId;
    @ManyToOne
    @JoinColumn(name = "aircompany_id", referencedColumnName = "id", nullable = false)
    private Aircompany aircompanyByAircompanyId;
    @OneToMany(mappedBy = "airplaneByAirplaneId")
    private Collection<Route> routesById;
    @OneToMany(mappedBy = "airplaneByAirplaneId")
    private Collection<Seat> seatsById;
}
