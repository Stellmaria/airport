package com.academy.airport.entity.ticket;

import com.academy.airport.entity.route.Route;
import com.academy.airport.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    private Integer id;
    private User user;
    private Route route;
    private String seatNo;
    private BigDecimal cost;
}
