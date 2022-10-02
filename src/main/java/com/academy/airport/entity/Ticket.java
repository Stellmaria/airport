package com.academy.airport.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ticket {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic
    @Column(name = "route_id", nullable = false)
    private Integer routeId;
    @Basic
    @Column(name = "seat_no", nullable = false, length = 4)
    private String seatNo;
    @Basic
    @Column(name = "cost", precision = 2)
    private BigDecimal cost;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userByUserId;
    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "id", nullable = false)
    private Route routeByRouteId;
}
