package com.academy.airport.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login {
    private Integer id;
    private User user;
    private String login;
    private String password;
}
