package com.academy.airport.dto;

import com.academy.airport.entity.Login;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Login} entity
 */
@Data
public class LoginDto implements Serializable {
    private final Integer id;
    private final Integer userId;
    private final String login;
    private final String password;
    private final UserDto userByUserId;
}