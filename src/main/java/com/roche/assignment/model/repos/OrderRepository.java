package com.roche.assignment.model.repos;

import com.roche.assignment.model.Order;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

@RepositoryRestResource
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    List<Order> findByCreatedBetween(@DateTimeFormat(pattern = "yyyy-MM-dd") @Param("from") LocalDate from,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @Param("to") LocalDate to);

}
