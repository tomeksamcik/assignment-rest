package com.roche.assignment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import com.roche.assignment.model.Order;
import com.roche.assignment.model.Product;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    public void totalAmountTest() {
        List<Product> products = Arrays.asList(new Product[]{ Product.builder().price(1.1f).build(), Product.builder().price(1.2f).build() });
        Order order = Order.builder().products(products).build();

        assertThat(order.getTotalAmount(), closeTo(2.3f, 0.001));
    }
}
