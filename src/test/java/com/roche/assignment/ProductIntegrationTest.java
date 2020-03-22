package com.roche.assignment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

import com.roche.assignment.model.Product;
import com.roche.assignment.model.repos.ProductRepository;
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

@DisplayName("Product endpoint")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIntegrationTest {

    private final String protocol = "http";

    private final String host = "localhost";

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Given entity doesn't exist when called POST /products will create the entity and respond with CREATED")
    public void testPost() {
        assertThat(getStream(productRepository).filter(p -> p.getName().equals("Product 1")).findFirst().isPresent(),
                equalTo(false));

        ResponseEntity<Product> product = restTemplate
                .postForEntity(String.format("%s://%s:%d/products", protocol, host, port),
                        Product.builder().name("Product 1").price(1.1f).build(), Product.class);

        Optional<Product> created = getStream(productRepository).filter(p -> p.getName().equals("Product 1"))
                .findFirst();
        assertThat(created.isPresent(), equalTo(true));
        assertThat(created.get().getName(), equalTo("Product 1"));
        assertThat(product.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(product.getBody().getName(), equalTo("Product 1"));
        assertThat(product.getBody().getPrice(), equalTo(1.1f));
    }

    @Test
    @DisplayName("Given entity doesn't exist when called POST /products with blank required field will not create the entity and respond with CREATED")
    public void testPostBlankName() {
        assertThat(getStream(productRepository).filter(p -> p.getPrice().equals(1.1f)).findFirst().isPresent(),
                equalTo(false));

        ResponseEntity<Product> product = restTemplate
                .postForEntity(String.format("%s://%s:%d/products", protocol, host, port),
                        Product.builder().price(1.1f).build(), Product.class);

        Optional<Product> created = getStream(productRepository).filter(p -> p.getPrice().equals(1.1f)).findFirst();
        assertThat(created.isPresent(), equalTo(false));
        assertThat(product.getStatusCode(), not(equalTo(HttpStatus.CREATED)));
    }

    @Test
    @DisplayName("Given entity exists when called GET /products/{id} will respond with OK")
    public void testGetExisting() {
        productRepository.save(Product.builder().name("Product 1").price(1.1f).build());
        Long id = getStream(productRepository).map(p -> p.getId()).findFirst().get();

        ResponseEntity<Product> product = restTemplate
                .getForEntity(String.format("%s://%s:%d/products/%d", protocol, host, port, id), Product.class);

        assertThat(product.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(product.getBody().getName(), equalTo("Product 1"));
        assertThat(product.getBody().getPrice(), equalTo(1.1f));
    }

    @Test
    @DisplayName("Given entity doesn't exist when called GET /products/{id} will respond with NOT_FOUND")
    public void testGetNonExisting() {
        ResponseEntity<Product> product = restTemplate
                .getForEntity(String.format("%s://%s:%d/products/1", protocol, host, port), Product.class);

        assertThat(product.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Given entities exist when called GET /products will respond with OK and a list of products")
    public void testGetAll() {
        productRepository.save(Product.builder().id(1l).name("Product 1").price(1.1f).build());
        productRepository.save(Product.builder().id(2l).name("Product 2").price(1.2f).build());

        ResponseEntity<String> product = restTemplate
                .getForEntity(String.format("%s://%s:%d/products", protocol, host, port), String.class);

        assertThat(product.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(product.getBody(), containsString("Product 1"));
        assertThat(product.getBody(), containsString("Product 2"));
    }

    @Test
    @DisplayName("Given entity exists when called PUT /products/{id} will update given entity and respond with OK")
    public void testPut() {
        productRepository.save(Product.builder().name("Product 1").price(1.1f).build());
        Long id = getStream(productRepository).map(p -> p.getId()).findFirst().get();

        restTemplate.put(String.format("%s://%s:%d/products/%d", protocol, host, port, id),
                Product.builder().name("Product Updated").price(1.1f).build());

        Optional<Product> updated = getStream(productRepository).findFirst();
        assertThat(updated.isPresent(), equalTo(true));
        assertThat(updated.get().getName(), equalTo("Product Updated"));
    }

    @Test
    @DisplayName("Given entity exists when called PUT /products/{id} with blank name will not update given entity")
    public void testPutWithBlankName() {
        productRepository.save(Product.builder().id(1l).name("Product 1").price(1.1f).build());

        restTemplate.put(String.format("%s://%s:%d/products/1", protocol, host, port),
                Product.builder().price(1.1f).build());

        Optional<Product> updated = getStream(productRepository).findFirst();
        assertThat(updated.isPresent(), equalTo(true));
        assertThat(updated.get().getName(), equalTo("Product 1"));
    }

    @Test
    @DisplayName("Given entity exists when called DELETE /products/{id} will soft delete given entity and respond with OK")
    public void testDelete() {
        productRepository.save(Product.builder().id(1l).name("Product 1").price(1.1f).build());

        restTemplate.delete(String.format("%s://%s:%d/products/1", protocol, host, port));

        Optional<Product> updated = getStream(productRepository).findFirst();
        assertThat(updated.isPresent(), equalTo(true));
        assertThat(updated.get().getDeleted(), equalTo(false));
    }

    private Stream<Product> getStream(ProductRepository repository) {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

}
