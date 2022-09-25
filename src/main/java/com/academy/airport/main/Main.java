package com.academy.airport.main;

import com.academy.airport.dao.impl.AircompanyDao;
import com.academy.airport.dao.impl.AirplaneDao;
import com.academy.airport.dao.impl.AirportDao;
import com.academy.airport.dao.impl.CityDao;
import com.academy.airport.dao.impl.CountryDao;
import com.academy.airport.dao.impl.LoginDao;
import com.academy.airport.dao.impl.RouteDao;
import com.academy.airport.dao.impl.SeatDao;
import com.academy.airport.dao.impl.TicketDao;
import com.academy.airport.dao.impl.UserDao;
import com.academy.airport.entity.airport.Aircompany;
import com.academy.airport.entity.airport.Airplane;
import com.academy.airport.entity.airport.Airport;
import com.academy.airport.entity.airport.Seat;
import com.academy.airport.entity.route.City;
import com.academy.airport.entity.route.Country;
import com.academy.airport.entity.route.FlightStatus;
import com.academy.airport.entity.route.Route;
import com.academy.airport.entity.ticket.Ticket;
import com.academy.airport.entity.user.Gender;
import com.academy.airport.entity.user.Login;
import com.academy.airport.entity.user.Role;
import com.academy.airport.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) {
        AircompanyDao aircompanyDao = AircompanyDao.getInstance();
        AirplaneDao airplaneDao = AirplaneDao.getInstance();
        AirportDao airportDao = AirportDao.getInstance();
        CityDao cityDao = CityDao.getInstance();
        CountryDao countryDao = CountryDao.getInstance();
        LoginDao loginDao = LoginDao.getInstance();
        RouteDao routeDao = RouteDao.getInstance();
        SeatDao seatDao = SeatDao.getInstance();
        TicketDao ticketDao = TicketDao.getInstance();
        UserDao userDao = UserDao.getInstance();

//        out.println(aircompanyDao.delete(1));
//        out.println(airplaneDao.delete(1));
//        out.println(airportDao.delete("MNK"));
//        out.println(cityDao.delete(1));
//        out.println(countryDao.delete(1));
//        out.println(routeDao.delete(1L));
//        out.println(loginDao.delete(1));
//        out.println(seatDao.delete(1));
//        out.println(ticketDao.delete(1L));
//        out.println(userDao.delete(1));

//        User user = User.builder()
//                .firstName("Михаил")
//                .lastName("Михайлович")
//                .passportNo("QO2450")
//                .birthday(LocalDate.of(2001, 5, 3))
//                .email("mike@gmail.com")
//                .role(Role.USER)
//                .gender(Gender.MALE)
//                .build();
//
//        var savedUser = userDao.save(user);
//        out.println(savedUser);
//
//        Login login = Login.builder()
//                .user(User.builder()
//                        .id(2)
//                        .build())
//                .login("mike")
//                .password("456")
//                .build();
//        var savedLogin = loginDao.save(login);
//        out.println(savedLogin);
//
//        Aircompany aircompany = Aircompany.builder()
//                .name("Emirates")
//                .build();
//
//        var savedAircompany = aircompanyDao.save(aircompany);
//        out.println(savedAircompany);
//
//        Airplane airplane = Airplane.builder()
//                .model("Ty-154")
//                .aircompany(Aircompany.builder()
//                        .id(2)
//                        .build())
//                .build();
//
//        var savedAirplane = airplaneDao.save(airplane);
//        out.println(savedAirplane);
//
//        Country country = Country.builder()
//                .name("United States")
//                .build();
//        var savedCountry = countryDao.save(country);
//        out.println(savedCountry);
//
//        City city = City.builder()
//                .name("New York")
//                .country(Country.builder()
//                        .id(3)
//                        .build())
//                .build();
//
//        var savedCity = cityDao.save(city);
//        out.println(savedCity);
//
//        Airport airport = Airport.builder()
//                .code("NYK")
//                .city(City.builder()
//                        .id(3)
//                        .build())
//                .build();
//
//        var savedAirport = airportDao.save(airport);
//        out.println(savedAirport);
//
//        Route route = Route.builder()
//                .departureDate(LocalDateTime.of(2022, 9, 26, 14, 0))
//                .departureAirportCode(Airport.builder()
//                        .code("NYK")
//                        .build())
//                .arrivalDate(LocalDateTime.of(2022, 9, 26, 18, 0))
//                .arrivalAirportCode(Airport.builder()
//                        .code("MNK")
//                        .build())
//                .airplane(Airplane.builder()
//                        .id(2)
//                        .build())
//                .status(FlightStatus.ARRIVED)
//                .build();
//
//        var savedRout = routeDao.save(route);
//        out.println(savedRout);

//        Ticket ticket = Ticket.builder()
//                .id(2)
//                .user(User.builder()
//                        .id(2)
//                        .build())
//                .route(Route.builder()
//                        .id(2L)
//                        .build())
//                .seatNo("A1")
//                .cost(BigDecimal.valueOf(200.00))
//                .build();
//        var savedTicket = ticketDao.save(ticket);
//        out.println(savedTicket);

//        Seat seat = Seat.builder()
//                .airplane(Airplane.builder()
//                        .id(2)
//                        .build())
//                .seatNo("E1")
//                .build();
//
//        var savedSeat = seatDao.save(seat);
//        out.println(savedSeat);

//        var optionalAircompany = aircompanyDao.findById(1);
//        out.println(optionalAircompany);
//        optionalAircompany.ifPresent(aircompany -> {
//            aircompany.setName("Belavia-1");
//            aircompanyDao.update(aircompany);
//        });
//        out.println(optionalAircompany);
//        out.println(aircompanyDao.findAll());
    }
}
