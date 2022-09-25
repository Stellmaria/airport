package com.academy.airport.entity.airport;

import com.academy.airport.entity.route.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {
    private String code;
    private City city;
}
