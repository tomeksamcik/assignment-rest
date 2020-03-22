package com.roche.assignment.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Please provide a name")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Please provide a price")
    private Float price;

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    private LocalDate created = LocalDate.now(ZoneOffset.UTC);

}
