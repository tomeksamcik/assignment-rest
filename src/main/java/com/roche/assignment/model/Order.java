package com.roche.assignment.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@Builder
@Table(name = "order_")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Email
    @Column(nullable = false)
    @NotEmpty(message = "Please provide an email")
    private String email;

    @ManyToMany
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Builder.Default
    private LocalDate created = LocalDate.now(ZoneOffset.UTC);

    @Transient
    public Double getTotalAmount() {
        return products.stream().mapToDouble(p -> p.getPrice()).sum();
    }

}
