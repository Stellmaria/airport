package com.academy.airport.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Collection;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;
    @Basic
    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;
    @Basic
    @Column(name = "passport_no", nullable = false, length = 32)
    private String passportNo;
    @Basic
    @Column(name = "birthday", nullable = false)
    private Date birthday;
    @Basic
    @Column(name = "email", nullable = false, length = 124)
    private String email;
    @Basic
    @Column(name = "role", nullable = false, length = 32)
    private String role;
    @Basic
    @Column(name = "gender", nullable = false, length = 16)
    private String gender;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Login> loginsById;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Ticket> ticketsById;
}
