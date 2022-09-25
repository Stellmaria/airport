package com.academy.airport.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String passportNo;
    private LocalDate birthday;
    private String email;
    private Role role;
    private Gender gender;
}
