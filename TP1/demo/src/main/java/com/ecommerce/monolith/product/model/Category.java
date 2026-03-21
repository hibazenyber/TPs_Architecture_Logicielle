package com.ecommerce.monolith.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Cette partie est optionnelle mais utile :
    // Elle permet de voir la liste des produits depuis une catégorie.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}