package com.academy.airport.entity.airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airplane {
    private Integer id;
    private String model;
    private Aircompany aircompany;
}
