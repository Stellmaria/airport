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
public class Airport {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "code", nullable = false, length = 3)
    private String code;
    @Basic
    @Column(name = "city_id", nullable = false)
    private Integer cityId;
    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private City cityByCityId;
    @OneToMany(mappedBy = "airportByDepartureAirportCode")
    private Collection<Route> routesByCode;
    @OneToMany(mappedBy = "airportByArrivalAirportCode")
    private Collection<Route> routesByCode0;
}
