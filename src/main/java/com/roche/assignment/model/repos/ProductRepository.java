package com.roche.assignment.model.repos;

import com.roche.assignment.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    @Modifying
    @Query("update Product p set deleted = true where p = :p")
    void delete(Product p);

    @Query("select p FROM Product p WHERE p.deleted = false")
    Page<Product> findAll(Pageable pageable);

}
