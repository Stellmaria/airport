package com.academy.airport.dao;

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
import com.academy.airport.entity.Aircompany;
import com.academy.airport.entity.Airplane;
import com.academy.airport.entity.Airport;
import com.academy.airport.entity.City;
import com.academy.airport.entity.Country;
import com.academy.airport.entity.Login;
import com.academy.airport.entity.Route;
import com.academy.airport.entity.Seat;
import com.academy.airport.entity.SeatPk;
import com.academy.airport.entity.Ticket;
import com.academy.airport.entity.User;
import com.academy.airport.util.ConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class DaoIntegrationTest {
    private final CountryDao countryDao = CountryDao.getInstance();
    private final CityDao cityDao = CityDao.getInstance();
    private final AirportDao airportDao = AirportDao.getInstance();
    private final AircompanyDao aircompanyDao = AircompanyDao.getInstance();
    private final AirplaneDao airplaneDao = AirplaneDao.getInstance();
    private final SeatDao seatDao = SeatDao.getInstance();
    private final RouteDao routeDao = RouteDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private final LoginDao loginDao = LoginDao.getInstance();
    private final TicketDao ticketDao = TicketDao.getInstance();

    private Integer countryId;
    private Integer cityId;
    private String airportCode;
    private Integer aircompanyId;
    private Integer airplaneId;
    private SeatPk seatKey;
    private Long routeId;
    private Integer userId;
    private Integer loginId;
    private Long ticketId;

    @Test
    void connectionPoolReusesConnectionsAfterClose() throws Exception {
        for (int i = 0; i < 20; i++) {
            try (var connection = ConnectionManager.get();
                 var statement = connection.prepareStatement("SELECT 1");
                 var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
            }
        }
    }

    @Test
    void allDaosSupportExpectedCrudOperations() throws Exception {
        var suffix = Long.toString(System.nanoTime());

        var country = countryDao.save(Country.builder().name("Testland-" + suffix).build());
        countryId = country.getId();
        assertTrue(countryDao.findById(countryId).isPresent());
        country.setName("Testland-updated-" + suffix);
        countryDao.update(country);
        assertEquals(country.getName(), countryDao.findById(countryId).orElseThrow().getName());
        assertTrue(countryDao.findAll().stream().anyMatch(item -> item.getId().equals(countryId)));

        var city = cityDao.save(City.builder().countryId(countryId).name("Test City-" + suffix).build());
        cityId = city.getId();
        city.setName("Test City Updated-" + suffix);
        cityDao.update(city);
        assertEquals(city.getName(), cityDao.findById(cityId).orElseThrow().getName());

        airportCode = "TST";
        var airport = airportDao.save(Airport.builder().code(airportCode.toLowerCase()).cityId(cityId).build());
        assertEquals(airportCode, airport.getCode());
        airportDao.update(airport);
        assertEquals(cityId, airportDao.findById(airportCode).orElseThrow().getCityId());

        var aircompany = aircompanyDao.save(Aircompany.builder().name("Test Air-" + suffix).build());
        aircompanyId = aircompany.getId();
        aircompany.setName("Test Air Updated-" + suffix);
        aircompanyDao.update(aircompany);
        assertEquals(aircompany.getName(), aircompanyDao.findById(aircompanyId).orElseThrow().getName());

        var airplane = airplaneDao.save(Airplane.builder()
                .model("Test Plane-" + suffix)
                .aircompanyId(aircompanyId)
                .build());
        airplaneId = airplane.getId();
        airplane.setModel("Test Plane Updated-" + suffix);
        airplaneDao.update(airplane);
        try (var connection = ConnectionManager.get()) {
            assertEquals(airplane.getModel(), airplaneDao.findById(airplaneId, connection).orElseThrow().getModel());
        }

        var seat = seatDao.save(Seat.builder().airplaneId(airplaneId).seatNo("Z9").build());
        seatKey = new SeatPk(airplaneId, seat.getSeatNo());
        assertTrue(seatDao.findById(seatKey).isPresent());
        assertTrue(seatDao.findAllByAirplaneId(airplaneId).stream()
                .anyMatch(item -> item.getSeatNo().equals("Z9")));
        assertThrows(UnsupportedOperationException.class, () -> seatDao.update(seat));

        var route = routeDao.save(Route.builder()
                .departureDate(Timestamp.valueOf("2030-01-01 10:00:00"))
                .departureAirportCode("MNK")
                .arrivalDate(Timestamp.valueOf("2030-01-01 12:00:00"))
                .arrivalAirportCode("LDN")
                .airplaneId(airplaneId)
                .status("SCHEDULED")
                .build());
        routeId = route.getId();
        route.setStatus("DEPARTED");
        routeDao.update(route);
        assertEquals("DEPARTED", routeDao.findById(routeId).orElseThrow().getStatus());

        var user = userDao.save(User.builder()
                .firstName("Test")
                .lastName("Passenger")
                .passportNo("PASS-" + suffix)
                .birthday(Date.valueOf("1990-01-01"))
                .email("airport-" + suffix + "@example.com")
                .role("user")
                .gender("other")
                .build());
        userId = user.getId();
        user.setLastName("Passenger Updated");
        userDao.update(user);
        assertEquals("Passenger Updated", userDao.findById(userId).orElseThrow().getLastName());

        var login = loginDao.save(Login.builder()
                .userId(userId)
                .login("airport-test-" + suffix)
                .password("$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W")
                .build());
        loginId = login.getId();
        login.setLogin("airport-test-updated-" + suffix);
        loginDao.update(login);
        assertTrue(loginDao.findByLogin(login.getLogin()).isPresent());

        var ticket = ticketDao.save(Ticket.builder()
                .userId(userId)
                .routeId(routeId)
                .seatNo("Z9")
                .cost(new BigDecimal("123.45"))
                .build());
        ticketId = ticket.getId();
        ticket.setCost(new BigDecimal("130.00"));
        ticketDao.update(ticket);
        assertEquals(0, new BigDecimal("130.00").compareTo(ticketDao.findById(ticketId).orElseThrow().getCost()));

        var duplicateSeatTicket = Ticket.builder()
                .userId(userId)
                .routeId(routeId)
                .seatNo("Z9")
                .cost(BigDecimal.TEN)
                .build();
        assertThrows(Exception.class, () -> ticketDao.save(duplicateSeatTicket));

        var nonexistentSeatTicket = Ticket.builder()
                .userId(userId)
                .routeId(routeId)
                .seatNo("Y9")
                .cost(BigDecimal.TEN)
                .build();
        assertThrows(Exception.class, () -> ticketDao.save(nonexistentSeatTicket));

        assertFalse(ticketDao.findAll().isEmpty());
        assertFalse(loginDao.findAll().isEmpty());
        assertFalse(routeDao.findAll().isEmpty());
        assertFalse(userDao.findAll().isEmpty());
    }

    @AfterEach
    void cleanUp() {
        if (ticketId != null) {
            ticketDao.delete(ticketId);
        }
        if (loginId != null) {
            loginDao.delete(loginId);
        }
        if (userId != null) {
            userDao.delete(userId);
        }
        if (routeId != null) {
            routeDao.delete(routeId);
        }
        if (seatKey != null) {
            seatDao.delete(seatKey);
        }
        if (airplaneId != null) {
            airplaneDao.delete(airplaneId);
        }
        if (aircompanyId != null) {
            aircompanyDao.delete(aircompanyId);
        }
        if (airportCode != null) {
            airportDao.delete(airportCode);
        }
        if (cityId != null) {
            cityDao.delete(cityId);
        }
        if (countryId != null) {
            countryDao.delete(countryId);
        }
    }
}
