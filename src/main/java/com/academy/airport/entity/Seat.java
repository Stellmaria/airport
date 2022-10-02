package com.academy.airport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(SeatPk.class)
public class Seat {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "airplane_id", nullable = false)
    private Integer airplaneId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "seat_no", nullable = false, length = 4)
    private String seatNo;
    @ManyToOne
    @JoinColumn(name = "airplane_id", referencedColumnName = "id", nullable = false)
    private Airplane airplaneByAirplaneId;
}
