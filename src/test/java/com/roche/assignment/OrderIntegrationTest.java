package com.roche.assignment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

import com.roche.assignment.model.Order;
import com.roche.assignment.model.repos.OrderRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("Order endpoint")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderIntegrationTest {

    private final String protocol = "http";

    private final String host = "localhost";

    @LocalServerPort
    private int port;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("Given entity doesn't exist when called POST /orders will create the entity and respond with CREATED")
    public void testPost() {
        assertThat(getStream(orderRepository).filter(o -> o.getEmail().equals("test@email")).findFirst().isPresent(),
                equalTo(false));

        ResponseEntity<Order> order = restTemplate
                .postForEntity(String.format("%s://%s:%d/orders", protocol, host, port), Order
                        .builder().email("test@email").build(), Order.class);

        Optional<Order> created = getStream(orderRepository).filter(o -> o.getEmail().equals("test@email")).findFirst();
        assertThat(created.isPresent(), equalTo(true));
        assertThat(created.get().getEmail(), equalTo("test@email"));
        assertThat(order.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(order.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    @DisplayName("Given entity doesn't exist when called POST /orders with blank required field will not create the entity and respond with CREATED")
    public void testPostBlankName() {
        assertThat(getStream(orderRepository).filter(p -> p.getEmail().equals("test@email")).findFirst().isPresent(),
                equalTo(false));

        ResponseEntity<Order> order = restTemplate
                .postForEntity(String.format("%s://%s:%d/orders", protocol, host, port), Order.builder().build(),
                        Order.class);

        Optional<Order> created = getStream(orderRepository).filter(p -> p.getEmail().equals("test@email")).findFirst();
        assertThat(created.isPresent(), equalTo(false));
        assertThat(order.getStatusCode(), not(equalTo(HttpStatus.CREATED)));
    }

    @Test
    @DisplayName("Given entity doesn't exist when called POST /orders with incorrect email field will not create the entity and respond with CREATED")
    public void testPostIncorrectEmail() {
        assertThat(getStream(orderRepository).filter(p -> p.getEmail().equals("not an email")).findFirst().isPresent(),
                equalTo(false));

        ResponseEntity<Order> order = restTemplate
                .postForEntity(String.format("%s://%s:%d/orders", protocol, host, port),
                        Order.builder().email("not an email").build(), Order.class);

        Optional<Order> created = getStream(orderRepository).filter(p -> p.getEmail().equals("not an email"))
                .findFirst();
        assertThat(created.isPresent(), equalTo(false));
        assertThat(order.getStatusCode(), not(equalTo(HttpStatus.CREATED)));
    }

    @Test
    @DisplayName("Given entities exist when called GET /orders will respond with OK and a list of orders")
    public void testGetAll() {
        orderRepository.save(Order.builder().email("test1@email").build());
        orderRepository.save(Order.builder().email("test2@email").build());

        ResponseEntity<String> orders = restTemplate
                .getForEntity(String.format("%s://%s:%d/orders", protocol, host, port), String.class);

        assertThat(orders.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(orders.getBody(), containsString("test1@email"));
        assertThat(orders.getBody(), containsString("test2@email"));
    }

    @Test
    @DisplayName("Given entities exist when called GET /orders/search/findByCreatedBetween?from=...&to=... will respond with OK and a list of orders for the given range")
    public void testGetBetweenDatesExisting() {
        orderRepository.save(Order.builder().email("test1@email").created(LocalDate.now()).build());
        orderRepository.save(Order.builder().email("test2@email").created(LocalDate.now()).build());

        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        ResponseEntity<String> orders = restTemplate.getForEntity(
                String.format("%s://%s:%d/orders/search/findByCreatedBetween?from=%s&to=%s", protocol, host, port,
                        LocalDate.now().format(dtf), LocalDate.now().format(dtf)), String.class);

        assertThat(orders.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(orders.getBody(), containsString("test1@email"));
        assertThat(orders.getBody(), containsString("test2@email"));
    }

    @Test
    @DisplayName("Given entities doesn't exist when called GET /orders/search/findByCreatedBetween?from=...&to=... will respond with OK and an empty list of orders for the given range")
    public void testGetBetweenDatesNonExisting() {
        orderRepository.save(Order.builder().email("test1@email").created(LocalDate.now().minusYears(1)).build());
        orderRepository.save(Order.builder().email("test2@email").created(LocalDate.now().minusYears(2)).build());

        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        ResponseEntity<String> orders = restTemplate.getForEntity(
                String.format("%s://%s:%d/orders/search/findByCreatedBetween?from=%s&to=%s", protocol, host, port,
                        LocalDate.now().format(dtf), LocalDate.now().format(dtf)), String.class);

        assertThat(orders.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(orders.getBody(), not(containsString("test1@email")));
        assertThat(orders.getBody(), not(containsString("test2@email")));
    }

    private Stream<Order> getStream(OrderRepository repository) {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

}
