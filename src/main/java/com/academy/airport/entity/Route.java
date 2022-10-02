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

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Route {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "departure_date", nullable = false)
    private Timestamp departureDate;
    @Basic
    @Column(name = "departure_airport_code", nullable = false, length = 3)
    private String departureAirportCode;
    @Basic
    @Column(name = "arrival_date", nullable = false)
    private Timestamp arrivalDate;
    @Basic
    @Column(name = "arrival_airport_code", nullable = false, length = 3)
    private String arrivalAirportCode;
    @Basic
    @Column(name = "airplane_id", nullable = false)
    private Integer airplaneId;
    @Basic
    @Column(name = "status", nullable = false, length = 32)
    private String status;
    @ManyToOne
    @JoinColumn(name = "departure_airport_code", referencedColumnName = "code", nullable = false)
    private Airport airportByDepartureAirportCode;
    @ManyToOne
    @JoinColumn(name = "arrival_airport_code", referencedColumnName = "code", nullable = false)
    private Airport airportByArrivalAirportCode;
    @ManyToOne
    @JoinColumn(name = "airplane_id", referencedColumnName = "id", nullable = false)
    private Airplane airplaneByAirplaneId;
    @OneToMany(mappedBy = "routeByRouteId")
    private Collection<Ticket> ticketsById;
}
