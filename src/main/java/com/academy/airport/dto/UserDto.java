package com.academy.airport.dto;

import com.academy.airport.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

/**
 * A DTO for the {@link User} entity
 */
@Data
public class UserDto implements Serializable {
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final String passportNo;
    private final Date birthday;
    private final String email;
    private final String role;
    private final String gender;
    private final Collection<LoginDto> loginsById;
    private final Collection<TicketDto> ticketsById;
}