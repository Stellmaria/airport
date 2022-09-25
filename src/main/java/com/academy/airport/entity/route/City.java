package com.academy.airport.entity.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {
    private Integer id;
    private Country country;
    private String name;
}
